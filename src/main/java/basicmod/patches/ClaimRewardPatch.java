package basicmod.patches;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.rewards.RewardItem;

@SpirePatch(clz = RewardItem.class, method = "claimReward")
public class ClaimRewardPatch {
    @SpirePostfixPatch
    public static boolean Postfix(boolean __result, RewardItem __instance) {
        if (__result) {
            // 当这个物品被成功领取后，根据 relicLink 寻找所有互相绑定的互斥奖励并将其标记为已完成（销毁）。
            // 游戏原生只往下一个节点寻找一次，这里改为循环遍历直到回到自己或遇到空节点，实现了更强的群组互斥支持。
            RewardItem ptr = __instance.relicLink;
            while (ptr != null && ptr != __instance) {
                ptr.isDone = true;
                ptr.ignoreReward = true;
                ptr = ptr.relicLink;
            }
        }
        return __result;
    }
}
