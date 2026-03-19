package basicmod.relics;

import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.input.InputHelper;

import static basicmod.BasicMod.makeID;

public class RabbitTalisman extends BaseRelic {
    public static final String NAME = "RabbitTalisman";
    public static final String ID = makeID(NAME);
    private static final RelicTier RARITY = RelicTier.RARE;
    private static final LandingSound SOUND = LandingSound.MAGICAL;

    private boolean activated = false;
    private boolean usedThisTurn = false;

    public RabbitTalisman() {
        super(ID, NAME, RARITY, SOUND);
        this.counter = 0;
    }

    @Override
    public void atPreBattle() {
        this.activated = false;
        this.usedThisTurn = false;
        this.grayscale = false;
        stopPulse();
    }

    @Override
    public void atTurnStart() {
        this.usedThisTurn = false;
        if (this.counter > 0) {
            this.counter--;
        }
        if (this.counter <= 0) {
            this.grayscale = false;
        }
    }

    @Override
    public void update() {
        super.update();
        // 战斗内、本回合尚未触发效果且不在 CD 中
        if (canInteractInCombat() && !this.usedThisTurn && this.counter <= 0) {

            // 1. 处理对我自己的右键点击：切换激活状态
            if (this.hb.hovered && InputHelper.justClickedRight) {
                this.activated = !this.activated;
                if (this.activated) {
                    CardCrawlGame.sound.play("UI_CLICK_1");
                    this.beginLongPulse();
                } else {
                    CardCrawlGame.sound.play("UI_CLICK_2");
                    this.stopPulse();
                }
            }

            // 2. 处理对其他遗物的右键点击：重置 CD/刷新效果
            if (this.activated && InputHelper.justClickedRight) {
                for (com.megacrit.cardcrawl.relics.AbstractRelic r : AbstractDungeon.player.relics) {
                    if (r != this && r.hb.hovered) {
                        if (tryRefreshRelic(r)) {
                            triggerEffect2();
                            break;
                        }
                    }
                }
            }
        }
    }

    private boolean tryRefreshRelic(com.megacrit.cardcrawl.relics.AbstractRelic r) {
        // CD 型遗物 (counter > 0)
        if (r.counter > 0) {
            r.counter = 0;
            if (r instanceof BaseRelic) {
                r.grayscale = false;
            }
            r.flash();
            return true;
        }
        // 自动触发型/阶段型 (已由我方添加特定的重置方法)
        if (r instanceof RatTalisman) {
            ((RatTalisman) r).resetUsedThisTurn();
            return true;
        }
        if (r instanceof HorseTalisman) {
            ((HorseTalisman) r).resetUsedThisTurn();
            return true;
        }

        // 猴符咒已移除手动触发功能，所以不需要这个判断。老版本猴符咒有战局内变换卡片的功能。
        // if (r instanceof MonkeyTalisman) {
        // ((MonkeyTalisman) r).triggerManually();
        // return true;
        // }

        if (r instanceof OxTalisman) {
            ((OxTalisman) r).resetUsedThisTurn();
            return true;
        }
        if (r instanceof PigTalisman) {
            ((PigTalisman) r).resetUsedThisTurn();
            return true;
        }
        // 对于其他使用了 grayscale 标识已使用的遗物，尝试恢复其可用状态
        if (r.grayscale) {
            r.grayscale = false;
            r.flash();
            return true;
        }

        return false;
    }

    private void triggerEffect1() {
        this.flash();
        this.activated = false;
        this.usedThisTurn = true;
        this.stopPulse();
        // 效果1：本回合不再有 CD 消耗，直接记录 triggered
    }

    private void triggerEffect2() {
        this.flash();
        this.activated = false;
        this.usedThisTurn = true;
        this.stopPulse();
        this.counter = 3; // 进入 3 回合冷却
        this.grayscale = true;
        addToBot(new RelicAboveCreatureAction(AbstractDungeon.player, this));
    }

    @Override
    public void onUseCard(AbstractCard targetCard, UseCardAction useCardAction) {
        // 如果激活了且本回合没用过，打出的是有费牌
        if (this.activated && !this.usedThisTurn && this.counter <= 0) {
            boolean hasCost = (targetCard.costForTurn > 0 && !targetCard.freeToPlayOnce)
                    || (targetCard.cost == -1 && targetCard.energyOnUse > 0);

            if (hasCost) {
                triggerEffect1();
                addToBot(new RelicAboveCreatureAction(AbstractDungeon.player, this));
                // 简单的实现：直接返还能量。严格的实现可以用 Patch 改 card.freeToPlayOnce
                if (targetCard.costForTurn > 0) {
                    addToBot(new GainEnergyAction(targetCard.costForTurn));
                } else if (targetCard.cost == -1) {
                    addToBot(new GainEnergyAction(targetCard.energyOnUse));
                }
            }
        }
    }

    @Override
    public void onVictory() {
        this.activated = false;
        this.usedThisTurn = false;
        this.grayscale = false;
        if (this.counter > 0)
            this.counter = 0;
        stopPulse();
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }
}
