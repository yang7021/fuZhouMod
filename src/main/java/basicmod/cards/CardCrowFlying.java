package basicmod.cards;

import basicmod.util.CardStats;
import basicmod.enums.CharacterEnums;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class CardCrowFlying extends BaseCard {
    public static final String ID = makeID("CrowFlying");
    private static final CardStats info = new CardStats(
            CharacterEnums.SHENGZHU_COLOR,
            CardType.ATTACK,
            CardRarity.UNCOMMON,
            CardTarget.ENEMY,
            1);

    public CardCrowFlying() {
        super(ID, info);
        // 造成 9 点伤害，升级变为 12 (+3)
        setDamage(9, 3);
        // 抽 1 张牌，升级抽 2 张 (+1)
        setMagic(1, 1);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new DamageAction(m, new DamageInfo(p, damage, damageTypeForTurn),
                com.megacrit.cardcrawl.actions.AbstractGameAction.AttackEffect.BLUNT_HEAVY));
        addToBot(new DrawCardAction(p, magicNumber));
    }
}
