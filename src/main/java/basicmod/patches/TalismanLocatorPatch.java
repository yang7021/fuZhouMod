package basicmod.patches;

import basicmod.helpers.TalismanHelper;
import basicmod.relics.TalismanLocator;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.screens.CombatRewardScreen;

public class TalismanLocatorPatch {

    @SpirePatch(clz = CombatRewardScreen.class, method = "setupItemReward")
    public static class SetupItemRewardPatch {
        @SpirePostfixPatch
        public static void Postfix(CombatRewardScreen __instance) {
            if (AbstractDungeon.player != null && AbstractDungeon.player.hasRelic(TalismanLocator.ID)) {

                RewardItem originalRelic = null;
                RewardItem sapphireKey = null;
                boolean hasTalismanReward = false;

                for (RewardItem reward : __instance.rewards) {
                    if (reward.type == RewardItem.RewardType.RELIC && reward.relic != null) {
                        if (TalismanHelper.ALL_TALISMAN_IDS.contains(reward.relic.relicId)) {
                            hasTalismanReward = true;
                        } else if (originalRelic == null) { // 获取列表中第一个非符咒的普通遗物
                            originalRelic = reward;
                        }
                    } else if (reward.type == RewardItem.RewardType.SAPPHIRE_KEY) {
                        sapphireKey = reward;
                    }
                }

                if (originalRelic != null && !hasTalismanReward) {
                    AbstractRelic missingTalisman = TalismanHelper.getRandomMissingTalisman();
                    if (missingTalisman != null) {
                        RewardItem talismanReward = new RewardItem(missingTalisman);

                        // 插入到原本遗物下方
                        int index = __instance.rewards.indexOf(originalRelic);
                        __instance.rewards.add(index + 1, talismanReward);

                        // 建立互斥链接
                        if (sapphireKey != null && originalRelic.relicLink == sapphireKey) {
                            // 游戏原生中有蓝钥匙的话原本是 originalRelic <-> sapphireKey
                            // 我们改为一个三环，从而让三个奖励互相连锁销毁
                            // 按照 Y轴 顺序，画出来的双链线是: 原本遗物 -> 符咒 -> 蓝钥匙
                            originalRelic.relicLink = talismanReward;
                            talismanReward.relicLink = sapphireKey;
                            sapphireKey.relicLink = originalRelic;
                        } else {
                            // 只有原遗物和符咒互斥
                            originalRelic.relicLink = talismanReward;
                            talismanReward.relicLink = originalRelic;
                        }

                        AbstractRelic locator = AbstractDungeon.player.getRelic(TalismanLocator.ID);
                        if (locator != null) {
                            locator.flash();
                        }
                    }
                }
            }
        }
    }
}
