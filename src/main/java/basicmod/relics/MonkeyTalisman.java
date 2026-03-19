package basicmod.relics;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;

import static basicmod.BasicMod.makeID;

/**
 * 猴符咒：七十二变 (正式版 v3)
 * 修改后的效果：在休息处选择休息（恢复生命值）后，可以从卡组里定向变幻一张卡片。
 * 流程：选一张现有的牌 -> 选一张本职业的所有牌 -> 永久替换。
 * 
 * 备注：
 * 1. 已移除战斗开始时的触发逻辑。
 * 2. 已解除与兔符咒的加速联动。
 * 3. 触发时机：在营火点击“休息”后。
 * 4. 体验优化：全流程使用“变幻”字眼，不提“删除”。
 */
public class MonkeyTalisman extends BaseRelic {
    public static final String NAME = "MonkeyTalisman";
    public static final String ID = makeID(NAME);
    private static final RelicTier RARITY = RelicTier.RARE;
    private static final LandingSound SOUND = LandingSound.MAGICAL;

    private enum State {
        IDLE, SELECTING_TARGET, SELECTING_REPLACEMENT
    }

    private State state = State.IDLE;
    private AbstractCard targetCard = null;

    public MonkeyTalisman() {
        super(ID, NAME, RARITY, SOUND);
    }

    @Override
    public void onRest() {
        // 当玩家在休息处选择“休息”后触发
        if (!AbstractDungeon.player.masterDeck.getPurgeableCards().isEmpty()) {
            this.flash();
            this.state = State.SELECTING_TARGET;
            // 第一步：让玩家选择一张现有的卡牌作为变幻的原型
            AbstractDungeon.gridSelectScreen.open(
                    AbstractDungeon.player.masterDeck.getPurgeableCards(), 
                    1, 
                    "选择一张卡牌进行“七十二变” (变幻原型)", 
                    false, false, false, false
            );
        }
    }

    @Override
    public void update() {
        super.update();
        
        // 第一阶段回调：选好了变幻的原型
        if (this.state == State.SELECTING_TARGET && !AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
            this.targetCard = AbstractDungeon.gridSelectScreen.selectedCards.get(0);
            AbstractDungeon.gridSelectScreen.selectedCards.clear();

            // 构建本职业全卡池供玩家挑选
            CardGroup group = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
            for (AbstractCard c : CardLibrary.getAllCards()) {
                // 筛选出本职业颜色的、非诅咒、非状态、非特殊牌
                if (c.color == AbstractDungeon.player.getCardColor() && 
                    c.type != AbstractCard.CardType.CURSE && 
                    c.type != AbstractCard.CardType.STATUS &&
                    c.rarity != AbstractCard.CardRarity.SPECIAL) {
                    group.addToBottom(c.makeStatEquivalentCopy());
                }
            }
            
            this.state = State.SELECTING_REPLACEMENT;
            // 第二步：打开网格选变幻后的模样
            AbstractDungeon.gridSelectScreen.open(group, 1, "选择变幻后的模样 (七十二变)", false);
        } 
        
        // 第二阶段回调：选好了目标卡牌
        else if (this.state == State.SELECTING_REPLACEMENT && !AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
            AbstractCard replacementCard = AbstractDungeon.gridSelectScreen.selectedCards.get(0);
            AbstractDungeon.gridSelectScreen.selectedCards.clear();
            this.state = State.IDLE;

            if (this.targetCard != null) {
                // 执行替换：从主卡组移除旧卡
                AbstractDungeon.player.masterDeck.removeCard(this.targetCard);
                // 展示新卡并加入主卡组
                AbstractDungeon.topLevelEffects.add(new ShowCardAndObtainEffect(
                        replacementCard.makeStatEquivalentCopy(), 
                        (float)Settings.WIDTH / 2.0F, 
                        (float)Settings.HEIGHT / 2.0F
                ));
            }
            
            this.flash();
            this.targetCard = null;
        }
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }
}
