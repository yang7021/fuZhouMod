package basicmod.powers;

import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;
import basicmod.BasicMod;

public class ValmontCommandPower extends AbstractPower {
    public static final String POWER_ID = BasicMod.makeID("ValmontCommandPower");
    public static final String NAME = "老板的指使";
    public static final String[] DESCRIPTIONS = { "本回合内你打出 #y葱姜蒜 和 #y特鲁 时，耗费变为 #b0 。" };

    public ValmontCommandPower(AbstractCreature owner, int amount) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = amount;
        this.type = PowerType.BUFF;
        this.isTurnBased = true; // 仅在本回合有效

        this.loadRegion("focus"); // 暂时用原版集中图片代替
        updateDescription();
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0];
    }

    // 在回合结束时自动移除本 Power
    @Override
    public void atEndOfTurn(boolean isPlayer) {
        if (isPlayer) {
            addToBot(new RemoveSpecificPowerAction(this.owner, this.owner, POWER_ID));
        }
    }
}
