package basicmod.relics;

import com.evacipated.cardcrawl.mod.stslib.relics.OnApplyPowerRelic;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;

import static basicmod.BasicMod.makeID;

public class SheepTalisman extends BaseRelic implements OnApplyPowerRelic {
    public static final String NAME = "SheepTalisman";
    public static final String ID = makeID(NAME);
    private static final RelicTier RARITY = RelicTier.RARE;
    private static final LandingSound SOUND = LandingSound.MAGICAL;

    public SheepTalisman() {
        super(ID, NAME, RARITY, SOUND);
    }

    @Override
    public boolean onApplyPower(AbstractPower power, AbstractCreature target, AbstractCreature source) {
        return true;
    }

    @Override
    public int onApplyPowerStacks(AbstractPower power, AbstractCreature target, AbstractCreature source,
            int stackAmount) {
        // 如果施加的是负面状态，且目标不是自己，并且来源是玩家（确保是玩家施加给敌人的debuff）
        if (power.type == AbstractPower.PowerType.DEBUFF && target != source && source == AbstractDungeon.player) {
            this.flash(); // 遗物闪烁提示触发
            power.amount *= 2; // 关键修复：除了返回双倍值，还需要直接修改将要施布的状态实例的数值
            power.updateDescription(); // 强制刷新该状态的文字描述，修复所有状态的文本不同步问题
            return stackAmount * 2; // 返回双倍的层数
        }
        return stackAmount; // 否则正常返回原始层数
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }
}
