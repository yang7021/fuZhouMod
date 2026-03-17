package basicmod.relics;

import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

import static basicmod.BasicMod.makeID;

public class OxTalisman extends BaseRelic {
    public static final String NAME = "OxTalisman";
    public static final String ID = makeID(NAME);
    private static final RelicTier RARITY = RelicTier.RARE;
    private static final LandingSound SOUND = LandingSound.HEAVY;

    private boolean activated = false;
    private boolean usedThisTurn = false;

    public OxTalisman() {
        super(ID, NAME, RARITY, SOUND);
    }

    @Override
    public void atPreBattle() {
        this.activated = false;
        this.usedThisTurn = false;
        this.grayscale = false;
        stopPulse();
    }

    @Override
    public void atTurnStart() {
        // 每个回合开始重置使用限制
        this.usedThisTurn = false;
        this.grayscale = false;
    }

    @Override
    public void update() {
        super.update();
        // 战斗内、本回合尚未使用，且鼠标悬停时右键点击
        if (AbstractDungeon.getCurrRoom() != null && AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT && !this.usedThisTurn) {
            if (this.hb.hovered && InputHelper.justClickedRight) {
                this.activated = !this.activated;
                if (this.activated) {
                    CardCrawlGame.sound.play("UI_CLICK_1");
                    this.beginLongPulse(); // 快速发光提示已激活
                } else {
                    CardCrawlGame.sound.play("UI_CLICK_2");
                    this.stopPulse();
                }
            }
        }
    }

    @Override
    public float atDamageModify(float damage, AbstractCard c) {
        // 如果已激活且是攻击牌
        if (this.activated && c.type == AbstractCard.CardType.ATTACK) {
            int str = 0;
            if (AbstractDungeon.player.hasPower("Strength")) {
                str = AbstractDungeon.player.getPower("Strength").amount;
            }
            // 我们的目标是最终伤害 = (基础伤害 + 力量) * 2
            // 杀塔的计算逻辑是：(遗物修改后的 damage) + 力量
            // 所以我们需要返回：(damage + 力量) * 2 - 力量
            return (damage + str) * 2.0F - str;
        }
        return damage;
    }

    @Override
    public void onUseCard(AbstractCard targetCard, UseCardAction useCardAction) {
        // 当打出的是攻击牌且处于激活状态时
        if (this.activated && targetCard.type == AbstractCard.CardType.ATTACK) {
            this.activated = false;
            this.usedThisTurn = true;
            this.grayscale = true; // 变灰标识已使用
            this.flash();
            this.stopPulse();
        }
    }

    @Override
    public void onVictory() {
        this.activated = false;
        this.usedThisTurn = false;
        this.grayscale = false;
        stopPulse();
    }

    /**
     * 重置牛符咒的使用标记，使其能在本回合再次触发
     */
    public void resetUsedThisTurn() {
        this.usedThisTurn = false;
        this.grayscale = false;
        this.flash();
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }
}
