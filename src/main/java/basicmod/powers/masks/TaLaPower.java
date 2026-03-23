package basicmod.powers.masks;

import basicmod.BasicMod;
import basicmod.actions.AddMaskCardAction;
import basicmod.powers.BasePower;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import java.util.ArrayList;

public class TaLaPower extends BasePower {
    public static final String POWER_ID = BasicMod.makeID("TaLaPower"); // ID Must match MaskManager constant
    private static final PowerType TYPE = PowerType.BUFF;
    private static final boolean TURN_BASED = false;

    public TaLaPower(AbstractCreature owner, int amount) {
        super(POWER_ID, TYPE, TURN_BASED, owner, amount);
        updateDescription();
    }

    @Override
    public void updateDescription() {
        PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(this.ID);
        if (powerStrings != null && powerStrings.DESCRIPTIONS != null && powerStrings.DESCRIPTIONS.length > 0) {
            this.name = powerStrings.NAME;
            this.description = powerStrings.DESCRIPTIONS[0] + this.amount + powerStrings.DESCRIPTIONS[1];
        }
    }

    @Override
    public void atStartOfTurnPostDraw() {
        super.atStartOfTurnPostDraw();
        this.flash();
        ArrayList<AbstractCard> cards = new ArrayList<>();
        cards.add(new basicmod.cards.shadowkhan.NiJiaNinja());
        cards.add(new basicmod.cards.shadowkhan.LaZuoBlade());
        cards.add(new basicmod.cards.shadowkhan.SaMoTroll());
        cards.add(new basicmod.cards.shadowkhan.BaTeBat());
        cards.add(new basicmod.cards.shadowkhan.KaBoPincer());
        cards.add(new basicmod.cards.shadowkhan.LeiSuAlien());
        cards.add(new basicmod.cards.shadowkhan.ManNiMantis());
        cards.add(new basicmod.cards.shadowkhan.MingTaShadow());
        cards.add(new basicmod.cards.shadowkhan.YiKaSamurai());
        
        AbstractCard rand = cards.get(AbstractDungeon.cardRandomRng.random(cards.size() - 1));
        AbstractDungeon.actionManager.addToBottom(new AddMaskCardAction(rand));
    }
}
