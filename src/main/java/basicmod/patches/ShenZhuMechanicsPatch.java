package basicmod.patches;
 
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.cards.AbstractCard;
import basicmod.powers.ShenZhuStatuePower;
 
public class ShenZhuMechanicsPatch {
 
    // 1. 护甲翻两倍 (由 3 倍改为 2 倍)
    @SpirePatch(clz = AbstractCreature.class, method = "addBlock")
    public static class DoubleBlockPatch {
        @SpirePrefixPatch
        public static void Prefix(AbstractCreature __instance, @ByRef int[] blockAmount) {
            // 如果获得护甲的是玩家，而且是圣主，而且身上有“圣主石像”的Power
            if (__instance != null && __instance instanceof AbstractPlayer
                    && __instance.hasPower(ShenZhuStatuePower.POWER_ID)) {
                // 最终取得的护甲直接乘 2
                blockAmount[0] *= 2;
            }
        }
    }
 
    // 2. 攻击牌费用增加 1 点，并直接显示在卡牌上
    @SpirePatch(clz = AbstractCard.class, method = "applyPowers")
    public static class CardCostApplyPowersPatch {
        @SpirePostfixPatch
        public static void Postfix(AbstractCard __instance) {
            modifyCost(__instance);
        }
    }
 
    @SpirePatch(clz = AbstractCard.class, method = "calculateCardDamage")
    public static class CardCostCalculateCardDamagePatch {
        @SpirePostfixPatch
        public static void Postfix(AbstractCard __instance, com.megacrit.cardcrawl.monsters.AbstractMonster mo) {
            modifyCost(__instance);
        }
    }
 
    private static void modifyCost(AbstractCard c) {
        // 仅对攻击牌生效，且玩家拥有圣主石像，且基础费用大于 0 (0费牌不涨价)
        if (c.type == AbstractCard.CardType.ATTACK && AbstractDungeon.player != null
                && AbstractDungeon.player.hasPower(ShenZhuStatuePower.POWER_ID)) {
            if (c.cost > 0) {
                // 稳健方案：涨价后的费用 = 基础费用 + 1
                // 这样无论 applyPowers 调用多少次，结果都是稳定的
                c.costForTurn = c.cost + 1;
                c.isCostModifiedForTurn = true;
            }
        }
    }
}
