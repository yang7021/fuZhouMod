package basicmod.relics;
 
import basemod.AutoAdd;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import static basicmod.BasicMod.makeID;

/**
 * 老版本猴符咒
 * 拾取时：选择卡组中 #b2 张牌进行 #y变化 。
 */
@AutoAdd.Ignore
public class MonkeyTalismanOld extends BaseRelic {
    public static final String NAME = "MonkeyTalismanOld";
    public static final String ID = makeID(NAME);
    private static final RelicTier RARITY = RelicTier.RARE;
    private static final LandingSound SOUND = LandingSound.MAGICAL;

    private enum SelectionState {
        IDLE, REMOVING, ADDING
    }

    private SelectionState state = SelectionState.IDLE;

    public MonkeyTalismanOld() {
        super(ID, NAME, RARITY, SOUND);
    }

    @Override
    public void onEquip() {
        state = SelectionState.REMOVING; // 进入删牌阶段
        // 隐藏不必要的UI元素，开启选择屏幕
        if (AbstractDungeon.isScreenUp) {
            AbstractDungeon.dynamicBanner.hide();
            AbstractDungeon.overlayMenu.cancelButton.hide();
            AbstractDungeon.previousScreen = AbstractDungeon.screen;
        }
        AbstractDungeon.getCurrRoom().phase = com.megacrit.cardcrawl.rooms.AbstractRoom.RoomPhase.INCOMPLETE;
        // 打开卡牌网格选择页面，允许选择2张牌进行删除
        AbstractDungeon.gridSelectScreen.open(AbstractDungeon.player.masterDeck.getPurgeableCards(), 2, "选择2张牌删除",
                false, false, false, false);
    }

    @Override
    public void update() {
        super.update();
        // 如果处于删牌阶段，且此时玩家选好了2张牌
        if (state == SelectionState.REMOVING && AbstractDungeon.gridSelectScreen.selectedCards.size() == 2) {
            // 删牌阶段选好后，执行删除逻辑
            for (AbstractCard card : AbstractDungeon.gridSelectScreen.selectedCards) {
                AbstractDungeon.player.masterDeck.removeCard(card); // 从卡组移除原卡牌
            }
            AbstractDungeon.gridSelectScreen.selectedCards.clear();

            // 生成供玩家自选的牌库 (生成72张不重复的全图鉴任何颜色卡池卡)
            com.megacrit.cardcrawl.cards.CardGroup group = new com.megacrit.cardcrawl.cards.CardGroup(
                    com.megacrit.cardcrawl.cards.CardGroup.CardGroupType.UNSPECIFIED);
            while (group.size() < 72) {
                // 获取任意职业颜色的随机卡牌
                AbstractCard randomCard = com.megacrit.cardcrawl.helpers.CardLibrary
                        .getAnyColorCard(AbstractDungeon.rollRarity()).makeCopy();
                boolean duplicate = false;
                for (AbstractCard c : group.group) {
                    if (c.cardID.equals(randomCard.cardID)) {
                        duplicate = true;
                        break;
                    }
                }
                // 确保不要生成诅咒牌和状态牌，且不重复
                if (!duplicate && randomCard.type != AbstractCard.CardType.CURSE
                        && randomCard.type != AbstractCard.CardType.STATUS) {
                    group.addToBottom(randomCard);
                }
            }

            state = SelectionState.ADDING; // 进入选牌阶段
            // 利用选牌网格复用展示72张全职业卡供玩家选2张
            AbstractDungeon.gridSelectScreen.open(group, 2, "随机七十二张牌中选择2张牌添加到卡组——地煞七十二变", false, false, false, false);

        } else if (state == SelectionState.ADDING && AbstractDungeon.gridSelectScreen.selectedCards.size() == 2) {
            // 如果处于加牌阶段，且玩家选好了2张牌，那么执行增加逻辑
            for (int i = 0; i < AbstractDungeon.gridSelectScreen.selectedCards.size(); i++) {
                AbstractCard card = AbstractDungeon.gridSelectScreen.selectedCards.get(i);
                float x = com.megacrit.cardcrawl.core.Settings.WIDTH / 2.0F
                        + (i == 0 ? -150.0F * com.megacrit.cardcrawl.core.Settings.scale
                                : 150.0F * com.megacrit.cardcrawl.core.Settings.scale);
                AbstractDungeon.effectsQueue.add(new com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect(
                        card.makeCopy(), x, com.megacrit.cardcrawl.core.Settings.HEIGHT / 2.0F));
            }
            AbstractDungeon.gridSelectScreen.selectedCards.clear(); // 清空选择列表
            state = SelectionState.IDLE; // 恢复正常状态
            AbstractDungeon.getCurrRoom().phase = com.megacrit.cardcrawl.rooms.AbstractRoom.RoomPhase.COMPLETE; // 恢复房间正常阶段
        }
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }
}
