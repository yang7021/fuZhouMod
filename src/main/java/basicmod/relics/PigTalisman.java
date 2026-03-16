package basicmod.relics;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

import static basicmod.BasicMod.makeID;

/**
 * 猪符咒
 */
public class PigTalisman extends BaseRelic {
    public static final String NAME = "PigTalisman";
    public static final String ID = makeID(NAME);
    private static final RelicTier RARITY = RelicTier.RARE;
    private static final LandingSound SOUND = LandingSound.CLINK;

    private boolean activated = false;
    private boolean usedThisTurn = false;

    public PigTalisman() {
        super(ID, NAME, RARITY, SOUND);
    }

    @Override
    public void atPreBattle() {
        usedThisTurn = false;
        activated = false;
        stopPulse();
    }

    @Override
    public void atTurnStart() {
        usedThisTurn = false;
        activated = false;
        stopPulse();
        this.grayscale = false;
    }

    @Override
    public void update() {
        super.update();
        // 如果在战斗中，未曾被使用，且鼠标悬停在遗物上并点击了右键
        if (AbstractDungeon.getCurrRoom() != null && AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT && !usedThisTurn) {
            if (this.hb.hovered && InputHelper.justClickedRight) {
                this.activated = !this.activated;
                if (this.activated) {
                    CardCrawlGame.sound.play("UI_CLICK_1");
                    this.beginLongPulse(); // 开始闪烁
                } else {
                    CardCrawlGame.sound.play("UI_CLICK_2");
                    this.stopPulse(); // 取消闪烁
                }
            }
        }
    }

    @Override
    public void onPlayCard(AbstractCard c, com.megacrit.cardcrawl.monsters.AbstractMonster m) {
        // 如果符咒已激发且打出的是攻击牌
        if (activated && c.type == AbstractCard.CardType.ATTACK) {
            activated = false;
            usedThisTurn = true;
            this.flash();
            this.stopPulse();
            this.grayscale = true; // 本回合不可再用
            
            // 将该牌本回合的伤害类型改为失去生命（HP_LOSS）
            // 在卡牌逻辑中，HP_LOSS 会无视护甲、无实体和虚弱等伤害修正
            c.damageTypeForTurn = DamageInfo.DamageType.HP_LOSS;
        }
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }
}
