package basicmod.actions;

import basicmod.enums.CustomTags;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import java.util.ArrayList;

public class ValmontAction extends AbstractGameAction {
    public ValmontAction() {
        this.actionType = ActionType.CARD_MANIPULATION;
    }

    @Override
    public void update() {
        // 获取所有可能受影响的卡牌堆
        ArrayList<AbstractCard> cards = new ArrayList<>();
        cards.addAll(AbstractDungeon.player.hand.group);
        cards.addAll(AbstractDungeon.player.drawPile.group);
        cards.addAll(AbstractDungeon.player.discardPile.group);

        for (AbstractCard c : cards) {
            if (shouldReduceCost(c)) {
                // setCostForTurn 会将费用设为 0，并在回合结束时由 StS 自动重置
                c.setCostForTurn(0);
            }
        }

        this.isDone = true;
    }

    private boolean shouldReduceCost(AbstractCard card) {
        // 识别逻辑：带有 afu 或 blackhand 标签的卡牌
        return card.tags.contains(CustomTags.afu) || card.tags.contains(CustomTags.blackhand);
    }
}
