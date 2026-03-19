package basicmod.cards.shadowkhan;

import basicmod.BasicMod;
import basicmod.enums.CharacterEnums;
import basicmod.enums.CustomTags;
import basicmod.helpers.MaskManager;
import basicmod.util.CardStats;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.PoisonPower; // Wait, curse? Using Poison as a placeholder for "Curse" or we should make a custom CursePower? User says "给予对手8层诅咒", STS has no "Curse" power natively, usually it's Poison for green or custom. Let's make a custom CursePower later, using generic for now.

public class LeiSuAlien extends BaseShadowKhanCard {
    public static final String ID = BasicMod.makeID(LeiSuAlien.class.getSimpleName());
    private static final CardStats info = new CardStats(
            CharacterEnums.SHENGZHU_COLOR,
            AbstractCard.CardType.SKILL,
            AbstractCard.CardRarity.SPECIAL,
            AbstractCard.CardTarget.ENEMY,
            0
    );

    public LeiSuAlien() {
        super(ID, info);
        setMagic(8, 4); // 8 curse, +4 on upgrade -> 12
        tags.add(CustomTags.SHADOW_KHAN);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        int totalCurse = magicNumber + (2 * MaskManager.shadowKhanCardsPlayedThisTurn);
        // We will apply a custom power here later: LeiSuCursePower
        addToBot(new ApplyPowerAction(m, p, new basicmod.powers.LeiSuCursePower(m, p, totalCurse), totalCurse));
    }
}
