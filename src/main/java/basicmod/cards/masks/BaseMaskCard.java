package basicmod.cards.masks;

import basicmod.cards.BaseCard;
import basicmod.enums.CustomTags;
import basicmod.powers.BaseMaskPower;
import basicmod.util.CardStats;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;

public abstract class BaseMaskCard extends BaseCard {
    public BaseMaskCard(String id, CardStats info) {
        super(id, info);
        tags.add(CustomTags.MASK);
        setMagic(1, 0); // Base masks all apply 1 layer of power
    }

    public abstract BaseMaskPower getMaskPower(AbstractPlayer p, int amount);
    public abstract String getMaskPowerId();

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        if (this.upgraded) {
            addToBot(new AbstractGameAction() {
                @Override
                public void update() {
                    AbstractPower pow = p.getPower(getMaskPowerId());
                    if (pow instanceof BaseMaskPower) {
                        ((BaseMaskPower) pow).upgradedAmount += magicNumber;
                    }
                    this.isDone = true;
                }
            });
        }
        BaseMaskPower pow = getMaskPower(p, magicNumber);
        if (pow != null && this.upgraded) {
            pow.upgradedAmount += magicNumber; // Ensure the initial stack also registers as upgraded
        }
        addToBot(new ApplyPowerAction(p, p, pow, magicNumber));
    }
}
