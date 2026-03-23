package basicmod.relics;

import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.ArtifactPower;

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
        if (canInteractInCombat() && !usedThisTurn) {
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
    public void onPlayCard(AbstractCard c, AbstractMonster m) {
        // 如果符咒已激发且打出的是攻击牌
        if (activated && c.type == AbstractCard.CardType.ATTACK) {
            activated = false;
            usedThisTurn = true;
            this.flash();
            this.stopPulse();
            this.grayscale = true; // 本回合不可再用

            // 针对全场生效的情况（群伤牌）
            if (c.target == AbstractCard.CardTarget.ALL_ENEMY || c.target == AbstractCard.CardTarget.ALL) {
                for (AbstractMonster mo : AbstractDungeon.getMonsters().monsters) {
                    if (!mo.isDeadOrEscaped()) {
                        removeBlockAndArtifact(mo);
                    }
                }
            } else if (m != null) {
                // 针对单体目标
                removeBlockAndArtifact(m);
            }
        }
    }

    private void removeBlockAndArtifact(AbstractMonster m) {
        // 移除护甲
        m.loseBlock();
        // 移除人工制品
        if (m.hasPower(ArtifactPower.POWER_ID)) {
            addToTop(new RemoveSpecificPowerAction(m, AbstractDungeon.player, ArtifactPower.POWER_ID));
        }
    }

    /**
     * 重置猪符咒的使用标记，使其能在本回合再次触发
     */
    public void resetUsedThisTurn() {
        this.usedThisTurn = false;
        this.grayscale = false;
        this.flash();
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }
}
