package basicmod.patches;

import basicmod.relics.SheepTalisman;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.core.EnergyManager;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

@SpirePatch(clz = EnergyManager.class, method = "recharge")
public class EnergyRetentionPatch {
    @SpirePrefixPatch
    public static SpireReturn<Void> Prefix(EnergyManager __instance) {
        // 如果玩家拥有羊符咒
        if (AbstractDungeon.player != null && AbstractDungeon.player.hasRelic(SheepTalisman.ID)) {
            // 不再检查是否有冰淇淋，羊符咒本身就该保证保留能量
            __instance.energy += __instance.energyMaster;
            com.megacrit.cardcrawl.ui.panels.EnergyPanel.totalCount = __instance.energy;
            return SpireReturn.Return(); // 跳过原版的 energy = energyMaster 逻辑
        }
        return SpireReturn.Continue();
    }
}
