package basicmod.relics;

import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;

import static basicmod.BasicMod.makeID;

public class RabbitTalisman extends BaseRelic {
    public static final String NAME = "RabbitTalisman";
    public static final String ID = makeID(NAME);
    private static final RelicTier RARITY = RelicTier.COMMON;
    private static final LandingSound SOUND = LandingSound.MAGICAL;

    private boolean isFirstCard = true;

    public RabbitTalisman() {
        super(ID, NAME, RARITY, SOUND);
    }

    @Override
    public void atTurnStart() {
        isFirstCard = true;
        this.beginPulse();
        this.pulse = true;
    }

    @Override
    public void onUseCard(AbstractCard targetCard, UseCardAction useCardAction) {
        if (isFirstCard) {
            isFirstCard = false;
            this.pulse = false;
            this.flash();
            addToBot(new RelicAboveCreatureAction(AbstractDungeon.player, this));

            // Refund the energy cost of the played card
            if (targetCard.costForTurn > 0 && !targetCard.freeToPlayOnce) {
                addToBot(new GainEnergyAction(targetCard.costForTurn));
            } else if (targetCard.cost == -1) {
                // For X cost cards, refund the energy consumed which is stored in energyOnUse
                addToBot(new GainEnergyAction(targetCard.energyOnUse));
            }
        }
    }

    @Override
    public void onVictory() {
        this.pulse = false;
        this.isFirstCard = true;
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }
}
