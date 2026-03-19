package basicmod.cards.shadowkhan;

import basicmod.BasicMod;
import basicmod.enums.CharacterEnums;
import basicmod.enums.CustomTags;
import basicmod.helpers.MaskManager;
import basicmod.util.CardStats;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class SaMoTroll extends BaseShadowKhanCard {
    public static final String ID = BasicMod.makeID(SaMoTroll.class.getSimpleName());
    private static final CardStats info = new CardStats(
            CharacterEnums.SHENGZHU_COLOR,
            AbstractCard.CardType.SKILL,
            AbstractCard.CardRarity.SPECIAL,
            AbstractCard.CardTarget.SELF,
            0
    );

    public SaMoTroll() {
        super(ID, info);
        setBlock(6, 0); // Base block 6
        setMagic(2, 1); // 2 bonus block per shadow khan, augments by +1 on upgrade -> 3
        tags.add(CustomTags.SHADOW_KHAN);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        int totalBlock = this.block + (this.magicNumber * MaskManager.shadowKhanCardsPlayedThisTurn);
        addToBot(new GainBlockAction(p, p, totalBlock));
        
        // This card itself is a Shadow Khan card, so after it is played, the counter will increment via common hook.
        // Wait, does it count itself? The doc says "本回合每打出一张【面具】召唤的【黑影兵团】牌，格挡增加 X 点。"
        // If it means "Previously played", then `cardsPlayedThisTurn` before this card increments.
        // If it means "Including itself", we should +1 or let the counter logic handle it depending on Hook timing.
        // Usually, `use` is called while the card is being played, before hook increments. 
    }
}
