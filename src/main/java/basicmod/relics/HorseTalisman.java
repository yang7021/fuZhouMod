package basicmod.relics;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import basicmod.powers.ShenZhuStatuePower;

import static basicmod.BasicMod.makeID;

public class HorseTalisman extends BaseRelic {
    public static final String NAME = "HorseTalisman";
    public static final String ID = makeID(NAME);
    private static final RelicTier RARITY = RelicTier.RARE;
    private static final LandingSound SOUND = LandingSound.MAGICAL;

    private boolean removedDebuffThisCombat = false;
    private boolean usedThisTurn = false;
    private boolean selectingPower = false;

    public HorseTalisman() {
        super(ID, NAME, RARITY, SOUND);
    }

    @Override
    public void atPreBattle() {
        removedDebuffThisCombat = false;
    }

    @Override
    public void atTurnStart() {
        this.usedThisTurn = false;
        this.grayscale = false;
    }

    @Override
    public void update() {
        super.update();

        // 1. 处理右键点击激活选择
        if (AbstractDungeon.getCurrRoom() != null && AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT && !this.usedThisTurn) {
            if (this.hb.hovered && InputHelper.justClickedRight) {
                // 查找所有负面状态
                CardGroup debuffs = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
                for (AbstractPower p : AbstractDungeon.player.powers) {
                    // 仅显示负面状态，且排除掉“圣主石像”这个核心机制（隐藏不让移除）
                    if (p.type == AbstractPower.PowerType.DEBUFF && !p.ID.equals(ShenZhuStatuePower.POWER_ID)) {
                        debuffs.addToBottom(new PowerPreviewCard(p));
                    }
                }

                if (!debuffs.isEmpty()) {
                    CardCrawlGame.sound.play("UI_CLICK_1");
                    this.selectingPower = true;
                    // 使用 7 参数多载版本：open(group, numCards, tipMsg, forUpgrade, forTransform, canCancel, forPurge)
                    AbstractDungeon.gridSelectScreen.open(debuffs, 1, "选择要移除的状态", false, false, true, false);
                    // 强制手动开启并设置取消文字
                    // 杀戮尖塔部分版本中，GridSelectScreen 的取消按钮字段名为 cancelBtn 且非 public
                    // 我们可以通过这种方式触发它的显示逻辑
                    AbstractDungeon.overlayMenu.cancelButton.show("取消");
                } else {
                    // 如果身上没 Debuff，晃动一下提示
                    this.flash();
                }
            }
        }

        // 2. 处理网格选择后的移除逻辑
        if (this.selectingPower) {
            // 情况 A: 玩家选了卡
            if (!AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
                AbstractCard selectedCard = AbstractDungeon.gridSelectScreen.selectedCards.get(0);
                AbstractDungeon.gridSelectScreen.selectedCards.clear();
                this.selectingPower = false;
                this.usedThisTurn = true;
                this.removedDebuffThisCombat = true; // 真正移除了才取消回血奖励
                this.grayscale = true;
                this.flash();

                if (selectedCard instanceof PowerPreviewCard) {
                    String powerId = ((PowerPreviewCard) selectedCard).powerId;
                    addToBot(new com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction(AbstractDungeon.player, AbstractDungeon.player, powerId));
                }
            }
            // 情况 B: 玩家取消了选择（通过取消按钮或 ESC）
            else if (AbstractDungeon.screen != AbstractDungeon.CurrentScreen.GRID) {
                this.selectingPower = false;
                // 不设置 usedThisTurn，不设置 removedDebuffThisCombat
            }
        }
    }

    /**
     * 重置马符咒的使用标记，使其能在本回合再次触发
     */
    public void resetUsedThisTurn() {
        this.usedThisTurn = false;
        this.grayscale = false;
        this.flash();
    }

    // 内部类：用于在 GridSelectScreen 中展示状态的“虚拟卡片”
    private static class PowerPreviewCard extends AbstractCard {
        public String powerId;

        public PowerPreviewCard(AbstractPower power) {
            super("PowerPreview", power.name, null, -2, formatDescription(power), CardType.SKILL, CardColor.COLORLESS, CardRarity.SPECIAL, CardTarget.NONE);
            this.powerId = power.ID;
            this.baseDamage = 0;
            this.baseBlock = 0;
            this.initializeDescription();
        }

        private static String formatDescription(AbstractPower power) {
            String desc = power.description;
            if (desc == null) return "";

            // 1. 卡片专用着色语法转换
            // 老大爷叮嘱：卡片展示状态信息时，#y #b 是遗物用的，卡片得用卡片专用方式 [#rrggbb]...[]
            desc = desc.replace("#y", "[#efc100]") 
                       .replace("#b", "[#71b1d1]") 
                       .replace("#r", "[#ff6563]") 
                       .replace("#g", "[#7fff00]");

            // 2. 针对常见状态进行描述美化（确保标签闭合）
            if (power.ID.equals("Vulnerable")) {
                desc = "受到来自 [#efc100]攻击[] 的伤害增加 [#71b1d1]50%[] 。 NL 持续 [#71b1d1]" + power.amount + "[] 回合。";
            } else if (power.ID.equals("Weakened")) {
                desc = "造成 [#efc100]攻击[] 的伤害减少 [#71b1d1]25%[] 。 NL 持续 [#71b1d1]" + power.amount + "[] 回合。";
            } else if (power.ID.equals("Frail")) {
                desc = "从卡牌获得的 [#efc100]格挡[] 减少 [#71b1d1]25%[] 。 NL 持续 [#71b1d1]" + power.amount + "[] 回合。";
            }

            // 3. 处理描述中的占位符闭合
            // 注意：卡片引擎认 * 关键字高亮，不认未闭合的色彩标签
            
            // 4. 清理多余空格
            desc = desc.replaceAll("\\s+", " ").trim();

            return desc;
        }

        @Override public void upgrade() {}
        @Override public void use(com.megacrit.cardcrawl.characters.AbstractPlayer p, com.megacrit.cardcrawl.monsters.AbstractMonster m) {}
        @Override public AbstractCard makeCopy() { return null; }
    }

    @Override
    public void onVictory() {
        // 战斗结束时，判断是否整场战斗中从未成功移除过负面状态
        if (!removedDebuffThisCombat) {
            this.flash();
            // 在战斗后回复最大生命值的20%
            AbstractDungeon.player.heal(AbstractDungeon.player.maxHealth / 5, true);
        }
        removedDebuffThisCombat = false; // 重置标记位，迎接下次战斗
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }
}
