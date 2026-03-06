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

    private boolean usedThisCombat = false;

    public RatTalisman() {
        super(ID, NAME, RARITY, SOUND);
    }

    @Override
    public void atPreBattle() {
        usedThisCombat = false; // 战斗前重置本场战斗已使用标记
    }

    @Override
    public void atTurnStartPostDraw() {
        if (!usedThisCombat) {
            usedThisCombat = true; // 仅限第一回合
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

                // 获取一张随机的攻击牌（修复原获取方式可能导致的报错异常）
                AbstractCard randomAttack = null;
                while (randomAttack == null || randomAttack.type != AbstractCard.CardType.ATTACK) {
                    randomAttack = AbstractDungeon.returnTrulyRandomCardInCombat().makeCopy();
                }

                this.flash(); // 遗物闪烁提示触发
                addToBot(new RelicAboveCreatureAction(AbstractDungeon.player, this));
                // 将生成的随机攻击牌加入手牌
                addToBot(new MakeTempCardInHandAction(randomAttack, 1));
            }
        }
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }
}
