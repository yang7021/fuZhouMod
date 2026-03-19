package basicmod.actions;

import basicmod.helpers.MaskManager;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.ExhaustSpecificCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import java.util.ArrayList;

public class MingTaExhaustAction extends AbstractGameAction {
    private AbstractMonster m;
    private int baseDamage;
    private AbstractCard sourceCard;
    private boolean isTargetFound = false;

    public MingTaExhaustAction(AbstractMonster target, int baseDamage, AbstractCard sourceCard) {
        this.actionType = ActionType.EXHAUST;
        this.duration = Settings.ACTION_DUR_FAST;
        this.m = target;
        this.baseDamage = baseDamage;
        this.sourceCard = sourceCard;
    }

    @Override
    public void update() {
        if (this.duration == Settings.ACTION_DUR_FAST) {
            ArrayList<AbstractCard> hand = AbstractDungeon.player.hand.group;
            ArrayList<AbstractCard> attacks = new ArrayList<>();
            for (AbstractCard c : hand) {
                if (c.type == AbstractCard.CardType.ATTACK && c != sourceCard) {
                    attacks.add(c);
                }
            }

            if (attacks.isEmpty()) {
                this.isDone = true;
                // Still do base damage
                int totalDamage = baseDamage + (MaskManager.mingTaTotalDamage / 2);
                AbstractDungeon.actionManager.addToTop(new DamageAction(m, new DamageInfo(AbstractDungeon.player, totalDamage, DamageInfo.DamageType.NORMAL), AttackEffect.SLASH_HEAVY));
                return;
            }

            if (attacks.size() == 1) {
                AbstractCard targetCard = attacks.get(0);
                exhaustAndDamage(targetCard);
                this.isDone = true;
                return;
            }

            // Exceeds 1, select a card. Need a hand select screen. 
            // For simplicity in this modding setup without full custom GridCardSelectAction callback:
            // Since it says "指定消耗手中一张攻击牌", we should use HandCardSelectScreen.
            AbstractDungeon.handCardSelectScreen.open("消耗1张攻击牌作为噬影团的祭品", 1, false, false, false, true, true);
            isTargetFound = true;
            tickDuration();
            return;
        }

        if (isTargetFound) {
            if (!AbstractDungeon.handCardSelectScreen.wereCardsRetrieved) {
                for (AbstractCard c : AbstractDungeon.handCardSelectScreen.selectedCards.group) {
                    exhaustAndDamage(c);
                }
                AbstractDungeon.handCardSelectScreen.wereCardsRetrieved = true;
                AbstractDungeon.handCardSelectScreen.selectedCards.group.clear();
                AbstractDungeon.player.hand.refreshHandLayout();
            }
            this.isDone = true;
        }
    }

    private void exhaustAndDamage(AbstractCard c) {
        // Track the exhausted attack's damage
        int dmg = c.baseDamage > 0 ? c.baseDamage : 0;
        MaskManager.mingTaTotalDamage += dmg;
        AbstractDungeon.actionManager.addToTop(new ExhaustSpecificCardAction(c, AbstractDungeon.player.hand));

        int totalDamage = baseDamage + (MaskManager.mingTaTotalDamage / 2);
        AbstractDungeon.actionManager.addToBottom(new DamageAction(m, new DamageInfo(AbstractDungeon.player, totalDamage, DamageInfo.DamageType.NORMAL), AttackEffect.SLASH_HEAVY));
    }
}
