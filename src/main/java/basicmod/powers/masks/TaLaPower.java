package basicmod.powers.masks;

import basicmod.BasicMod;
import basicmod.powers.BasePower;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import java.util.ArrayList;

public class TaLaPower extends BasePower {
    public static final String POWER_ID = BasicMod.makeID("TaLaPower"); // ID Must match MaskManager constant
    private static final PowerType TYPE = PowerType.BUFF;
    private static final boolean TURN_BASED = false;

    public TaLaPower(AbstractCreature owner, int amount) {
        super(POWER_ID, TYPE, TURN_BASED, owner, amount);
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
        AbstractDungeon.actionManager.addToBottom(new MakeTempCardInHandAction(rand, 1));
    }
}
