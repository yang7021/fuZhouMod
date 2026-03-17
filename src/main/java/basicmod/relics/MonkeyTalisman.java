package basicmod.relics;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;

import static basicmod.BasicMod.makeID;

/**
 * 猴符咒：七十二变 (临时版)
 * 战前先选一张要变的牌，再从职业卡池里自选一张变幻后的样子。
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
    private boolean forceTrigger = false;

    public MonkeyTalisman() {
        super(ID, NAME, RARITY, SOUND);
    }

    @Override
    public void atBattleStartPreDraw() {
        // 战斗发牌前触发
        this.flash();
        state = State.SELECTING_TARGET;
        
        // 第一步：从玩家克隆后的战斗卡组副本里选1张要变的（网格显示 masterDeck 比较稳妥）
        AbstractDungeon.gridSelectScreen.open(AbstractDungeon.player.masterDeck, 1, "选择一张牌临时进行“七十二变”", false);
    }

    @Override
    public void update() {
        super.update();
        
        // 兔符咒等可能强行触发“七十二变”
        if (this.state == State.IDLE && AbstractDungeon.gridSelectScreen.selectedCards.isEmpty() && forceTrigger) {
            forceTrigger = false;
            this.flash();
            state = State.SELECTING_TARGET;
            AbstractDungeon.gridSelectScreen.open(AbstractDungeon.player.masterDeck, 1, "再次选择一张牌临时进行“七十二变”", false);
        }

        // 第一步回调：选好了要变的“原型”
        if (state == State.SELECTING_TARGET && !AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
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
            
            state = State.SELECTING_REPLACEMENT;
            // 第二步：打开网格选变后的牌
            AbstractDungeon.gridSelectScreen.open(group, 1, "选择变幻后的模样", false);

        } 
        // 第二步回调：选好了变身后的模样
        else if (state == State.SELECTING_REPLACEMENT && !AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
            AbstractCard replacementCard = AbstractDungeon.gridSelectScreen.selectedCards.get(0);
            AbstractDungeon.gridSelectScreen.selectedCards.clear();
            state = State.IDLE;

            // 执行替换：在当前战斗的抽牌堆里把那张牌换掉
            if (targetCard != null) {
                replaceCardInGroup(AbstractDungeon.player.drawPile, targetCard, replacementCard);
            }
            this.flash();
            this.targetCard = null;
        }
    }

    private void replaceCardInGroup(CardGroup group, AbstractCard target, AbstractCard replacement) {
        for (int i = 0; i < group.size(); i++) {
            AbstractCard c = group.group.get(i);
            // 通过 ID 匹配，确保临时变身成功
            if (c.cardID.equals(target.cardID) && c.upgraded == target.upgraded) {
                group.group.set(i, replacement.makeStatEquivalentCopy());
                break; // 每次只变一张
            }
        }
    }

    /**
     * 强行在战斗中再次触发七十二变
     */
    public void triggerManually() {
        this.forceTrigger = true;
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }
}
