package basicmod.relics;

import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import java.util.ArrayList;

import static basicmod.BasicMod.makeID;

public class RatTalisman extends BaseRelic {
    public static final String NAME = "RatTalisman";
    public static final String ID = makeID(NAME);
    private static final RelicTier RARITY = RelicTier.COMMON;
    private static final LandingSound SOUND = LandingSound.MAGICAL;

    public RatTalisman() {
        super(ID, NAME, RARITY, SOUND);
    }

    @Override
    public void atTurnStartPostDraw() {
        if (!this.pulse) {
            this.pulse = true; // 使用 pulse 作为标记，表示“第一回合已经触发过”
            ArrayList<AbstractCard> candidates = new ArrayList<>();
            // 遍历玩家手牌，寻找状态牌或诅咒牌
            for (AbstractCard c : AbstractDungeon.player.hand.group) {
                if (c.type == AbstractCard.CardType.STATUS || c.type == AbstractCard.CardType.CURSE) {
                    candidates.add(c);
                }
            }

            if (!candidates.isEmpty()) {
                // 随机选择一张状态牌或诅咒牌
                AbstractCard toReplace = candidates.get(AbstractDungeon.cardRandomRng.random(candidates.size() - 1));
                // 从手牌中移除它
                AbstractDungeon.player.hand.removeCard(toReplace);

                // 修复BUG：获取一张战斗中随机的攻击牌代替 CardLibrary.getAnyColorCard
                AbstractCard randomAttack = AbstractDungeon.returnTrulyRandomCardInCombat(AbstractCard.CardType.ATTACK)
                        .makeCopy();

                this.flash(); // 遗物闪烁提示触发
                addToBot(new RelicAboveCreatureAction(AbstractDungeon.player, this));
                // 将生成的随机攻击牌加入手牌
                addToBot(new MakeTempCardInHandAction(randomAttack, 1));
            }
        }
    }

    @Override
    public void atPreBattle() {
        this.pulse = false;
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }
}
