package basicmod.relics;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import static basicmod.BasicMod.makeID;

public class RoosterTalisman extends BaseRelic {
    public static final String NAME = "RoosterTalisman";
    public static final String ID = makeID(NAME);
    private static final RelicTier RARITY = RelicTier.RARE;
    private static final LandingSound SOUND = LandingSound.MAGICAL;

    public RoosterTalisman() {
        super(ID, NAME, RARITY, SOUND);
    }

    @Override
    public void onEquip() {
        AbstractDungeon.player.masterHandSize += 2; // 获得遗物时手牌上限+2
    }

    @Override
    public void onUnequip() {
        AbstractDungeon.player.masterHandSize -= 2; // 失去遗物时手牌上限-2
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }
}
