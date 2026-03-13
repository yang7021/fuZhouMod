package basicmod.cards;

import basicmod.util.CardStats;
import basicmod.enums.CharacterEnums;
import basicmod.enums.CustomTags;
import basicmod.powers.TaiShanPressPower;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class CardTaiShanPress extends BaseCard {
    public static final String ID = makeID("TaiShanPress");
    private static final CardStats info = new CardStats(
            CharacterEnums.SHENGZHU_COLOR,
            CardType.POWER,
            CardRarity.UNCOMMON,
            CardTarget.SELF,
            2);

    private static final int BLOCK = 3;
    private static final int UPG_BLOCK = 1;

    public CardTaiShanPress() {
        super(ID, info);
        setMagic(BLOCK, UPG_BLOCK);
        tags.add(CustomTags.afu);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new ApplyPowerAction(p, p, new TaiShanPressPower(p, magicNumber), magicNumber));
    }
}
