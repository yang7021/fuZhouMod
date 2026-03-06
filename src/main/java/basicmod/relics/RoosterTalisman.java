package basicmod.relics;

import com.evacipated.cardcrawl.mod.stslib.relics.OnReceivePowerRelic;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;

import static basicmod.BasicMod.makeID;

public class RoosterTalisman extends BaseRelic implements OnReceivePowerRelic {
    public static final String NAME = "RoosterTalisman";
    public static final String ID = makeID(NAME);
    private static final RelicTier RARITY = RelicTier.COMMON;
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
    public boolean onReceivePower(AbstractPower power, AbstractCreature source) {
        // 如果获得的负面状态是“纠缠”(Entangled，对应原作特效)，则闪烁并免疫
        if ("Entangled".equals(power.ID)) {
            this.flash();
            return false; // 返回false表示拒绝获得此buff
        }
        return true;
    }

    @Override
    public int onReceivePowerStacks(AbstractPower power, AbstractCreature source, int stackAmount) {
        return stackAmount;
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }
}
