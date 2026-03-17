package basicmod.patches;

import basicmod.events.RobTalismanEvent;
import basicmod.helpers.TalismanHelper;
import basicmod.BasicMod;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractEvent;
import com.megacrit.cardcrawl.random.Random;

@SpirePatch(clz = AbstractDungeon.class, method = "generateEvent", paramtypez = { Random.class })
public class EventInterceptorPatch {
    @SpirePrefixPatch
    public static SpireReturn<AbstractEvent> Prefix(Random rng) {
        int count = 0;
        for (String id : TalismanHelper.ALL_TALISMAN_IDS) {
            if (AbstractDungeon.player.hasRelic(id)) {
                count++;
            }
        }

        // 老大爷，按您的新要求：每符咒 6% 概率
        float chance = count * 0.06f;
        boolean roll = chance > 0 && rng.randomBoolean(chance);

        // 只要这个方法被执行，就说明这确实是一个“事件”逻辑在运行，哪怕原本是要变商人/战斗
        BasicMod.logger.info(">>> [事件房内容判定] 开始! 符咒数: " + count + ", 劫持概率: " + (chance * 100) + "%, 结果: " + roll);

        if (roll) {
            BasicMod.logger.info(">>> [事件房内容判定] 劫持成功！强制返回 RobTalismanEvent。");
            return SpireReturn.Return(new RobTalismanEvent());
        }

        // 没中或者没符咒，就走原版的抽签流程
        return SpireReturn.Continue();
    }
}
