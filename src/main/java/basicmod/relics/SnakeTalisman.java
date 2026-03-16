package basicmod.relics;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.powers.IntangiblePlayerPower;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

import static basicmod.BasicMod.makeID;

public class SnakeTalisman extends BaseRelic {
    public static final String NAME = "SnakeTalisman";
    public static final String ID = makeID(NAME);
    private static final RelicTier RARITY = RelicTier.RARE;
    private static final LandingSound SOUND = LandingSound.MAGICAL;

    private boolean activated = false;

    public SnakeTalisman() {
        super(ID, NAME, RARITY, SOUND);
    }

    @Override
    public void atPreBattle() {
        this.counter = 0;
        this.activated = false;
        this.grayscale = false;
        stopPulse();
    }

    @Override
    public void atTurnStart() {
        // 回合开始时，如果正在冷却中，数字减 1
        if (this.counter > 0) {
            this.counter--;
            if (this.counter == 0) {
                this.grayscale = false; // 冷却结束，恢复彩色
            }
        }
    }

    @Override
    public void update() {
        super.update();
        // 只有在战斗中、非冷却状态下才允许点击
        if (AbstractDungeon.getCurrRoom() != null && AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT && this.counter <= 0) {
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
    public void onPlayerEndTurn() {
        if (this.activated) {
            this.activated = false;
            this.flash();
            this.stopPulse();
            
            // 给予 1 层无实体
            addToBot(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player,
                    new IntangiblePlayerPower(AbstractDungeon.player, 1), 1));
            
            // 进入 8 回合冷却
            this.counter = 8;
            this.grayscale = true;
        }
    }

    @Override
    public void onVictory() {
        this.counter = -1; // 战斗结束重置，但在本 mod 习惯中 counter 为 -1 通常表示数字消失
        this.grayscale = false;
        this.activated = false;
        stopPulse();
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }
}
