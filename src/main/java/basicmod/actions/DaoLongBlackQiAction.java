package basicmod.actions;

import basicmod.cards.BaseCard;
import basicmod.cards.CardWeThree;
import basicmod.cards.attacks.CardGanWenCui;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import java.util.ArrayList;
import java.util.List;

public class DaoLongBlackQiAction extends AbstractGameAction {
    private boolean upgraded;

    public DaoLongBlackQiAction(boolean upgraded) {
        this.upgraded = upgraded;
        this.actionType = ActionType.CARD_MANIPULATION;
        this.duration = Settings.ACTION_DUR_FAST;
    }

    @Override
    public void update() {
        if (this.duration == Settings.ACTION_DUR_FAST) {
            replaceInGroup(AbstractDungeon.player.hand);
            replaceInGroup(AbstractDungeon.player.drawPile);
            this.isDone = true;
        }
        tickDuration();
    }

    private void replaceInGroup(CardGroup group) {
        List<AbstractCard> toReplace = new ArrayList<>();
        for (AbstractCard c : group.group) {
            if (c.cardID.equals(CardWeThree.ID)) {
                toReplace.add(c);
            }
        }

        for (AbstractCard oldCard : toReplace) {
            AbstractCard newCard = new CardGanWenCui();
            if (upgraded) {
                newCard.upgrade();
            }
            
            // 保持原本的位置（如果是手牌，尽可能保持索引或简单替换）
            int index = group.group.indexOf(oldCard);
            if (index != -1) {
                group.group.set(index, newCard);
                // 触发卡牌变换的视觉效果或刷新
                newCard.applyPowers();
                if (group.type == CardGroup.CardGroupType.HAND) {
                    newCard.superFlash();
                }
            }
        }
    }
}
