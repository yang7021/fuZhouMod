package basicmod.powers;

import basicmod.helpers.MaskManager;
import com.megacrit.cardcrawl.actions.common.InstantKillAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class LeiSuCursePower extends BasePower {
    public static final String POWER_ID = basicmod.BasicMod.makeID("LeiSuCurse");
    private static final PowerType TYPE = PowerType.DEBUFF;
    private static final boolean TURN_BASED = false;

    public LeiSuCursePower(AbstractCreature owner, AbstractCreature source, int amount) {
        super(POWER_ID, TYPE, TURN_BASED, owner, amount);
        this.canGoNegative = false;
        // In localizations, add LeiSuCurse
    }

    @Override
    public void atEndOfRound() {
        if (this.owner != null && !this.owner.isDeadOrEscaped()) {
            if (this.owner.currentHealth <= this.amount) {
                this.flash();
                AbstractDungeon.actionManager.addToBottom(new InstantKillAction(this.owner));
            } else {
                // If it doesn't kill, it should probably clear or stay? "回合结束时若诅咒大于等于敌方血线则斩杀" doesn't say it clears, so it stays.
            }
        }
    }
}
