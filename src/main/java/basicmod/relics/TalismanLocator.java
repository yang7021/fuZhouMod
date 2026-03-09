package basicmod.relics;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import basicmod.helpers.TalismanHelper;
import java.util.ArrayList;

import static basicmod.BasicMod.makeID;

public class TalismanLocator extends BaseRelic {
    public static final String NAME = "TalismanLocator";
    public static final String ID = makeID(NAME);
    private static final RelicTier RARITY = RelicTier.RARE;
    private static final LandingSound SOUND = LandingSound.CLINK;

    public TalismanLocator() {
        super(ID, NAME, RARITY, SOUND);
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }
}
