package basicmod.cards;

import basicmod.util.CardStats;
import basicmod.enums.CharacterEnums;
import basicmod.powers.ValmontCommandPower;
import basicmod.enums.CustomTags;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class CardValmont extends BaseCard {
    public static final String ID = makeID("Valmont");
    private static final CardStats info = new CardStats(
            CharacterEnums.SHENGZHU_COLOR,
            CardType.SKILL,
            CardRarity.UNCOMMON,
            CardTarget.SELF,
            1);

    public CardValmont() {
        super(ID, info);
        tags.add(CustomTags.afu);
        setExhaust(true);
        setCostUpgrade(0);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 给玩家挂上“老板的指使”状态，持续一回合
        addToBot(new ApplyPowerAction(p, p, new ValmontCommandPower(p, 1), 1));
    }
}
