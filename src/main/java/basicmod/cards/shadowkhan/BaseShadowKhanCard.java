package basicmod.cards.shadowkhan;

import basicmod.cards.BaseCard;
import basicmod.enums.CustomTags;
import basicmod.util.CardStats;
import com.megacrit.cardcrawl.cards.AbstractCard;

public abstract class BaseShadowKhanCard extends BaseCard {
    public BaseShadowKhanCard(String id, CardStats info) {
        super(id, info);
        tags.add(CustomTags.SHADOW_KHAN);
        this.exhaust = true; // Most shadow khan cards probably shouldn't clog the deck permanently, though doc doesn't specify. Wait, doc says "增加一张到你手中", if they don't exhaust they stay in deck. Let's make them Exhaust by default.
        this.exhaust = true;
    }

    // In slay the spire, cards added to hand mid-combat usually exhaust, but if the doc doesn't say, I shouldn't assume.
}
