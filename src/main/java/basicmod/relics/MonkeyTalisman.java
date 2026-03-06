package basicmod.relics;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;

import static basicmod.BasicMod.makeID;

public class MonkeyTalisman extends BaseRelic {
    public static final String NAME = "MonkeyTalisman";
    public static final String ID = makeID(NAME);
    private static final RelicTier RARITY = RelicTier.RARE;
    private static final LandingSound SOUND = LandingSound.MAGICAL;

    private boolean cardsSelected = true;

    public MonkeyTalisman() {
        super(ID, NAME, RARITY, SOUND);
    }

    @Override
    public void onEquip() {
        cardsSelected = false; // 标记尚未完成卡牌选择
        // 隐藏不必要的UI元素，开启选择屏幕
        if (AbstractDungeon.isScreenUp) {
            AbstractDungeon.dynamicBanner.hide();
            AbstractDungeon.overlayMenu.cancelButton.hide();
            AbstractDungeon.previousScreen = AbstractDungeon.screen;
        }
        AbstractDungeon.getCurrRoom().phase = com.megacrit.cardcrawl.rooms.AbstractRoom.RoomPhase.INCOMPLETE;
        // 打开卡牌网格选择页面，允许选择2张牌进行变换
        AbstractDungeon.gridSelectScreen.open(AbstractDungeon.player.masterDeck.getPurgeableCards(), 2, "选择2张牌进行变换",
                false, false, false, false);
    }

    @Override
    public void update() {
        super.update();
        // 持续检查是否在选择屏幕中选好了2张牌
        if (!cardsSelected && AbstractDungeon.gridSelectScreen.selectedCards.size() == 2) {
            cardsSelected = true;
            // 遍历选中的卡牌
            for (int i = 0; i < AbstractDungeon.gridSelectScreen.selectedCards.size(); i++) {
                AbstractCard card = AbstractDungeon.gridSelectScreen.selectedCards.get(i);
                AbstractDungeon.player.masterDeck.removeCard(card); // 从卡组移除原卡牌
                AbstractDungeon.transformCard(card, false, AbstractDungeon.miscRng); // 变换为新卡牌
                AbstractCard transCard = AbstractDungeon.getTransformedCard();

                // 将变化后的卡牌分别错开显示在屏幕中间
                float x = Settings.WIDTH / 2.0F + (i == 0 ? -150.0F * Settings.scale : 150.0F * Settings.scale);
                AbstractDungeon.effectsQueue.add(new ShowCardAndObtainEffect(transCard, x, Settings.HEIGHT / 2.0F));
            }
            AbstractDungeon.gridSelectScreen.selectedCards.clear(); // 清空选择列表
            AbstractDungeon.getCurrRoom().phase = com.megacrit.cardcrawl.rooms.AbstractRoom.RoomPhase.COMPLETE; // 恢复房间正常阶段
        }
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }
}
