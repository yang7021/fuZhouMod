package basicmod.patches;

import basicmod.helpers.TalismanHelper;
import basicmod.relics.TalismanLocator;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.screens.CombatRewardScreen;

public class TalismanLocatorPatch {

    // 拦截 CombatRewardScreen.setupItemReward 方法
    // 在奖励生成界面准备好所有物品后执行
    @SpirePatch(clz = CombatRewardScreen.class, method = "setupItemReward")
    public static class SetupItemRewardPatch {
        @SpirePostfixPatch
        public static void Postfix(CombatRewardScreen __instance) {
            // 如果玩家拥有“符咒探测仪”遗物
            if (AbstractDungeon.player != null && AbstractDungeon.player.hasRelic(TalismanLocator.ID)) {

                // 检查当前的奖励列表中是否已经包含遗物奖励
                boolean hasRelicReward = false;

                // 为了防止每次刷新（如读档、某些Mod的影响）重复添加相同的符咒奖励
                // 我们也要检查是否已经添加了符咒奖励
                boolean hasTalismanReward = false;

                for (RewardItem reward : __instance.rewards) {
                    if (reward.type == RewardItem.RewardType.RELIC && reward.relic != null) {
                        hasRelicReward = true;
                        if (TalismanHelper.ALL_TALISMAN_IDS.contains(reward.relic.relicId)) {
                            hasTalismanReward = true;
                        }
                    }
                }

                // 如果当前战利品中有至少一个遗物，且我们还没追加过符咒，并且玩家未集齐12个符咒
                if (hasRelicReward && !hasTalismanReward) {
                    AbstractRelic missingTalisman = TalismanHelper.getRandomMissingTalisman();
                    if (missingTalisman != null) {
                        // 在奖励列表末尾追加这个符咒遗物
                        RewardItem talismanReward = new RewardItem(missingTalisman);
                        // 确保它是有效的遗物奖励
                        __instance.rewards.add(talismanReward);

                        // 让遗物闪烁以提示玩家
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
