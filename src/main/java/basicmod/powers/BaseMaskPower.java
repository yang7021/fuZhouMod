package basicmod.powers;

import basicmod.helpers.MaskManager;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;

public abstract class BaseMaskPower extends BasePower {
    
    public int upgradedAmount = 0;

    public BaseMaskPower(String id, PowerType powerType, boolean isTurnBased, AbstractCreature owner, int amount) {
        super(id, powerType, isTurnBased, owner, amount);
        updateDescription();
    }

    @Override
    public void updateDescription() {
        PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(this.ID);
        if (powerStrings != null && powerStrings.DESCRIPTIONS != null && powerStrings.DESCRIPTIONS.length > 0) {
            this.name = powerStrings.NAME;
            this.description = powerStrings.DESCRIPTIONS[0] + this.amount + powerStrings.DESCRIPTIONS[1];
        }
    }

    @Override
    public void onInitialApplication() {
        super.onInitialApplication();
        MaskManager.onMaskApplied(this.owner, this.ID, this);
    }

    @Override
    public void onRemove() {
        super.onRemove();
        MaskManager.onMaskRemoved(this.ID);
    }

    public abstract AbstractCard getLinkedShadowKhanCard();

    public void onEvict() {
        this.flash();
        int reg = this.amount - this.upgradedAmount;
        if (reg > 0) {
            AbstractCard card = getLinkedShadowKhanCard();
            if (card != null) {
                AbstractDungeon.actionManager.addToBottom(new MakeTempCardInHandAction(card, reg));
            }
        }
        if (this.upgradedAmount > 0) {
            AbstractCard card = getLinkedShadowKhanCard();
            if (card != null) {
                card.upgrade();
                AbstractDungeon.actionManager.addToBottom(new MakeTempCardInHandAction(card, this.upgradedAmount));
            }
        }
    }

    @Override
    public void atStartOfTurnPostDraw() {
        super.atStartOfTurnPostDraw();
        this.flash();
        int reg = this.amount - this.upgradedAmount;
        if (reg > 0) {
            AbstractCard card = getLinkedShadowKhanCard();
            if (card != null) {
                AbstractDungeon.actionManager.addToBottom(new MakeTempCardInHandAction(card, reg));
            }
        }
        if (this.upgradedAmount > 0) {
            AbstractCard card = getLinkedShadowKhanCard();
            if (card != null) {
                card.upgrade();
                AbstractDungeon.actionManager.addToBottom(new MakeTempCardInHandAction(card, this.upgradedAmount));
            }
        }
    }
}
