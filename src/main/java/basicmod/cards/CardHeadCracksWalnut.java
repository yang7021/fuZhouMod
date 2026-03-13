package basicmod.cards;

import basicmod.util.CardStats;
import basicmod.enums.CharacterEnums;
import basicmod.enums.CustomTags;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class CardHeadCracksWalnut extends BaseCard {
    public static final String ID = makeID("HeadCracksWalnut");
    private static final CardStats info = new CardStats(
            CharacterEnums.SHENGZHU_COLOR,
            CardType.ATTACK,
            CardRarity.COMMON,
            CardTarget.ENEMY,
            1);

    private static final int DAMAGE = 9;
    private static final int UPG_DAMAGE = 3;
    private static final int DRAW = 2;

    public CardHeadCracksWalnut() {
        super(ID, info);
        setDamage(DAMAGE, UPG_DAMAGE);
        setMagic(DRAW);
        tags.add(CustomTags.afu);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new DamageAction(m, new DamageInfo(p, damage, damageTypeForTurn), AbstractGameAction.AttackEffect.BLUNT_HEAVY));

        if (isComboActive()) {
            addToBot(new DrawCardAction(p, magicNumber));
        }
    }

    private boolean isComboActive() {
        if (AbstractDungeon.actionManager.cardsPlayedThisTurn.size() >= 2) {
            AbstractCard lastCard = AbstractDungeon.actionManager.cardsPlayedThisTurn.get(AbstractDungeon.actionManager.cardsPlayedThisTurn.size() - 2);
            return lastCard.tags.contains(CustomTags.afu);
        }
        return false;
    }

    @Override
    public void triggerOnGlowCheck() {
        this.glowColor = AbstractCard.BLUE_BORDER_GLOW_COLOR.cpy();
        if (isComboActive()) {
            this.glowColor = AbstractCard.GOLD_BORDER_GLOW_COLOR.cpy();
        }
    }
}
