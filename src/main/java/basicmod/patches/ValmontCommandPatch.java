package basicmod.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import basicmod.powers.ValmontCommandPower;
import basicmod.cards.CardWeThree;
import basicmod.cards.CardTohru;

public class ValmontCommandPatch {

    @SpirePatch(clz = AbstractCard.class, method = "hasEnoughEnergy")
    public static class ValmontZeroCostCheck {
        @SpirePrefixPatch
        // 这一层可以在核心判定前截胡
        public static SpireReturn<Boolean> Prefix(AbstractCard __instance) {
            // 如果玩家拥有“老板的指使”状态，且打出的是“葱姜蒜”或“特鲁”
            if (AbstractDungeon.player != null && AbstractDungeon.player.hasPower(ValmontCommandPower.POWER_ID)) {
                if (__instance.cardID.equals(CardWeThree.ID) || __instance.cardID.equals(CardTohru.ID)) {
                    // 当符合条件时，此卡这回合就必定当作 0 费来打出（不管之前能量判定多严格）
                    // 虽然费用显示可能没有变成0，但在这里直接允许打出。原版也可以直接通过修改 costForTurn 表现更好。

                    // 为了保险起见，如果这里让它通过，应该也要设置游玩一次耗费为 0
                    if (!AbstractDungeon.actionManager.turnHasEnded) {
                        // 返回true前，把当前所需费用强行设0，这样扣费也为0
                        __instance.freeToPlayOnce = true;
                    }
                }
            }
            return SpireReturn.Continue();
        }
    }
}
