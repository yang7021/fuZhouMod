package basicmod.cards;

import basicmod.util.CardStats;
import basicmod.enums.CharacterEnums;
import basicmod.actions.ValmontAction;
import basicmod.enums.CustomTags;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class CardValmont extends BaseCard {
    public static final String ID = makeID("Valmont");
    private static final CardStats info = new CardStats(
            CharacterEnums.SHENGZHU_COLOR,
            CardType.SKILL,
            CardRarity.RARE, // 已更新为金卡
            CardTarget.SELF,
            2);

    public CardValmont() {
        super(ID, info);
        tags.add(CustomTags.afu);
        setExhaust(true);
        setCostUpgrade(1); // 升级后-1费 (升1)
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 直接执行费用削减逻辑，不再产生可见能力
        addToBot(new ValmontAction());
    }
}
