package basicmod.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;
import basicmod.powers.ShenZhuStatuePower;

public class ShenZhuMechanicsPatch {

    // 1. 护甲翻三倍 (最终计算结果翻三倍)
    @SpirePatch(clz = AbstractCreature.class, method = "addBlock")
    public static class TripleBlockPatch {
        @SpirePrefixPatch
        public static void Prefix(AbstractCreature __instance, @ByRef int[] blockAmount) {
            // 如果获得护甲的是玩家，而且是圣主，而且身上有“圣主石像”的Power
            if (__instance != null && __instance instanceof AbstractPlayer
                    && __instance.hasPower(ShenZhuStatuePower.POWER_ID)) {
                // 最终取得的护甲直接乘 3
                blockAmount[0] *= 3;
            }
        }
    }

    // 2. 检查能否打出牌：针对攻击牌，费用翻倍进行验证
    @SpirePatch(clz = AbstractCard.class, method = "hasEnoughEnergy")
    public static class DoubleCostCheckPatch {
        @SpirePrefixPatch
        public static SpireReturn<Boolean> Prefix(AbstractCard __instance) {
            // 仅对攻击牌生效，且玩家拥有圣主石像
            if (__instance.type == AbstractCard.CardType.ATTACK && AbstractDungeon.player != null
                    && AbstractDungeon.player.hasPower(ShenZhuStatuePower.POWER_ID)) {

                // 如果回合已经结束，不让打出
                if (AbstractDungeon.actionManager.turnHasEnded) {
                    __instance.cantUseMessage = AbstractCard.TEXT[9];
                    return SpireReturn.Return(false);
                }

                // X耗能牌的情况
                if (__instance.costForTurn == -1 && __instance.energyOnUse < EnergyPanel.totalCount) {
                    __instance.energyOnUse = EnergyPanel.totalCount;
                }

                // 计算玩家打出该牌需要几费
                int requiredCost = __instance.costForTurn;

                if (__instance.freeToPlayOnce) {
                    requiredCost = 0; // 本次打出免费
                } else if (__instance.cost == -1) {
                    requiredCost = 0; // -1表示X耗费，实际上在这里需要的最低花费是0
                } else {
                    // 只要基础消费 > 0，翻倍。如果是0费牌，0*2 = 0，正好满足老大爷“0费不翻倍”的要求
                    requiredCost *= 2;
                }

                if (requiredCost > EnergyPanel.totalCount) {
                    __instance.cantUseMessage = AbstractCard.TEXT[11];
                    return SpireReturn.Return(false);
                }
            }
            // 返回 Continue 让后续基于新修正过的逻辑走原版的其它判定或者直接接管
            return SpireReturn.Continue();
        }
    }

    // 3. 真正扣除费用：如果翻倍了，要在打出卡牌之后多扣一份等价的能量
    @SpirePatch(clz = AbstractPlayer.class, method = "useCard")
    public static class UseCardDoubleCostPatch {
        @SpirePrefixPatch
        public static void Prefix(AbstractPlayer __instance, AbstractCard c,
                com.megacrit.cardcrawl.monsters.AbstractMonster monster, int energyOnUse) {
            if (c.type == AbstractCard.CardType.ATTACK && __instance.hasPower(ShenZhuStatuePower.POWER_ID)) {
                // 如果该卡本来就需花费大于0点，并且不是免费的，它原版系统会扣除1次 costForTurn
                // 我们通过这个 Patch，在原版扣除前，额外再扣一次 costForTurn，达到扣除 2 次的效果。
                if (c.costForTurn > 0 && !c.freeToPlayOnce && !c.isInAutoplay) {
                    __instance.energy.use(c.costForTurn);
                }
            }
        }
    }
}
