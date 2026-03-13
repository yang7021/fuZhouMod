package basicmod.cards;

import basicmod.util.CardStats;
import basicmod.enums.CharacterEnums;
import basicmod.enums.CustomTags;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import java.util.HashSet;
import java.util.Set;

public class CardAfuBeatJackie extends BaseCard {
    public static final String ID = makeID("AfuBeatJackie");
    private static final CardStats info = new CardStats(
            CharacterEnums.SHENGZHU_COLOR,
            CardType.ATTACK,
            CardRarity.RARE,
            CardTarget.ENEMY,
            3);

    private static final int DAMAGE = 28;
    private static final int UPG_DAMAGE = 10;
    private static final int BONUS_PER_CARD = 2;
    private static final int UPG_BONUS_PER_CARD = 1;

    public CardAfuBeatJackie() {
        super(ID, info);
        setDamage(DAMAGE, UPG_DAMAGE);
        setMagic(BONUS_PER_CARD, UPG_BONUS_PER_CARD);
        tags.add(CustomTags.afu);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new DamageAction(m, new DamageInfo(p, damage, damageTypeForTurn), AbstractGameAction.AttackEffect.BLUNT_HEAVY));
    }

    @Override
    public void applyPowers() {
        int realBaseDamage = this.baseDamage;
        int count = getUniqueAfuMoves();
        this.baseDamage += count * this.magicNumber;
        super.applyPowers();
        this.baseDamage = realBaseDamage;
        this.isDamageModified = (this.damage != this.baseDamage);
    }

    @Override
    public void calculateCardDamage(AbstractMonster m) {
        int realBaseDamage = this.baseDamage;
        int count = getUniqueAfuMoves();
        this.baseDamage += count * this.magicNumber;
        super.calculateCardDamage(m);
        this.baseDamage = realBaseDamage;
        this.isDamageModified = (this.damage != this.baseDamage);
    }

    private int getUniqueAfuMoves() {
        Set<String> uniqueIDs = new HashSet<>();
        for (AbstractCard c : AbstractDungeon.actionManager.cardsPlayedThisCombat) {
            if (c.tags.contains(CustomTags.afu)) {
                uniqueIDs.add(c.cardID);
            }
        }
        return uniqueIDs.size();
    }
}
