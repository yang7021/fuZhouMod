package basicmod.character;

import basemod.abstracts.CustomPlayer;
import basicmod.BasicMod;
import basicmod.enums.CharacterEnums;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.EnergyManager;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ScreenShake;
import com.megacrit.cardcrawl.screens.CharSelectInfo;
import com.megacrit.cardcrawl.events.city.Vampires;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.badlogic.gdx.math.MathUtils;
import java.util.ArrayList;

public class ShengZhuCustomPlayer extends CustomPlayer {

    public static final int ENERGY_PER_TURN = 3;
    public static final int STARTING_HP = 80;
    public static final int MAX_HP = 80;
    public static final int STARTING_GOLD = 99;
    public static final int CARD_DRAW = 5;
    public static final int ORB_SLOTS = 0;

    public ShengZhuCustomPlayer(String name, PlayerClass setClass) {
        super(name, setClass, new basicmod.util.ShengZhuEnergyOrb(),
                (String) null, (String) null);

        initializeClass(BasicMod.imagePath("character/shengzhu/statue.png"),
                BasicMod.imagePath("character/shengzhu/shoulder.png"),
                BasicMod.imagePath("character/shengzhu/shoulder.png"),
                BasicMod.imagePath("character/shengzhu/corpse.png"),
                getLoadout(), 20.0F, -10.0F, 220.0F, 290.0F, new EnergyManager(ENERGY_PER_TURN));

        this.dialogX = (this.drawX + 0.0F * Settings.scale);
        this.dialogY = (this.drawY + 220.0F * Settings.scale);
    }

    @Override
    public void render(SpriteBatch sb) {
        // 如果在篝火/营地界面，游戏引擎默认会用 512x512 且只画左下角区域
        // 我们在这里拦截一下，如果是营地且不是战斗，就按图片的真实比例画
        if (AbstractDungeon.getCurrRoom() instanceof com.megacrit.cardcrawl.rooms.RestRoom) {
            com.badlogic.gdx.graphics.Texture shoulderImg = this.animX > 0.0F ? this.shoulder2Img : this.shoulderImg;
            if (shoulderImg != null) {
                sb.setColor(Color.WHITE);
                sb.draw(shoulderImg,
                        this.drawX - (float) shoulderImg.getWidth() * Settings.scale / 2.0F,
                        this.drawY - 300.0F * Settings.scale,
                        (float) shoulderImg.getWidth() * Settings.scale,
                        (float) shoulderImg.getHeight() * Settings.scale);
            }
            this.hb.render(sb);
            this.healthHb.render(sb);
            return;
        }

        // 其它情况（如战斗、事件）走原本或自定义的 renderPlayerImage 逻辑
        super.render(sb);
    }

    @Override
    public void renderPlayerImage(SpriteBatch sb) {
        if (this.img != null) {
            sb.setColor(Color.WHITE);
            // 这里将图片居中放于角色坐标点
            sb.draw(this.img, this.drawX - this.img.getWidth() * Settings.scale / 2.0F + this.animX, this.drawY,
                    this.img.getWidth() * Settings.scale, this.img.getHeight() * Settings.scale,
                    0, 0, this.img.getWidth(), this.img.getHeight(), this.flipHorizontal, this.flipVertical);
        }
        this.hb.render(sb);
        this.healthHb.render(sb);
    }

    @Override
    public CharSelectInfo getLoadout() {
        return new CharSelectInfo("圣主", "远古恶魔，八大恶魔之一。",
                STARTING_HP, MAX_HP, ORB_SLOTS, STARTING_GOLD, CARD_DRAW, this, getStartingRelics(),
                getStartingDeck(), false);
    }

    @Override
    public ArrayList<String> getStartingDeck() {
        ArrayList<String> retVal = new ArrayList<>();
        // 初始套牌：4张葱姜蒜、4张特鲁、1张瓦龙、1张乌鸦坐飞机
        for (int i = 0; i < 4; i++) {
            retVal.add("fuZhouMod:WeThree");
            retVal.add("fuZhouMod:Tohru");
        }
        retVal.add("fuZhouMod:Valmont");
        retVal.add("fuZhouMod:CrowFlying");
        return retVal;
    }

    @Override
    public ArrayList<String> getStartingRelics() {
        ArrayList<String> retVal = new ArrayList<>();
        // 符咒定位仪是初始遗物
        retVal.add("fuZhouMod:TalismanLocator");
        return retVal;
    }

    @Override
    public void doCharSelectScreenSelectEffect() {
        CardCrawlGame.sound.playA("ATTACK_HEAVY", MathUtils.random(-0.2F, 0.2F));
        CardCrawlGame.screenShake.shake(ScreenShake.ShakeIntensity.LOW, ScreenShake.ShakeDur.SHORT, false);
    }

    @Override
    public String getCustomModeCharacterButtonSoundKey() {
        return "ATTACK_HEAVY";
    }

    @Override
    public int getAscensionMaxHPLoss() {
        return 5;
    }

    @Override
    public AbstractCard.CardColor getCardColor() {
        return CharacterEnums.SHENGZHU_COLOR;
    }

    @Override
    public Color getCardTrailColor() {
        return com.megacrit.cardcrawl.helpers.CardHelper.getColor(200.0f, 50.0f, 50.0f);
    }

    @Override
    public BitmapFont getEnergyNumFont() {
        return FontHelper.energyNumFontRed;
    }

    @Override
    public String getLocalizedCharacterName() {
        return "圣主";
    }

    @Override
    public com.badlogic.gdx.graphics.Texture getEnergyImage() {
        return basicmod.util.TextureLoader.getTexture(basicmod.BasicMod.imagePath("512/card_red_orb.png"));
    }

    @Override
    public AbstractCard getStartCardForEvent() {
        return new basicmod.cards.CardWeThree(); // Placeholder
    }

    @Override
    public String getTitle(PlayerClass playerClass) {
        return "圣主";
    }

    @Override
    public AbstractPlayer newInstance() {
        return new ShengZhuCustomPlayer("圣主", CharacterEnums.SHENGZHU);
    }

    @Override
    public Color getCardRenderColor() {
        return com.megacrit.cardcrawl.helpers.CardHelper.getColor(200.0f, 50.0f, 50.0f);
    }

    @Override
    public Color getSlashAttackColor() {
        return com.megacrit.cardcrawl.helpers.CardHelper.getColor(200.0f, 50.0f, 50.0f);
    }

    @Override
    public AbstractGameAction.AttackEffect[] getSpireHeartSlashEffect() {
        return new AbstractGameAction.AttackEffect[] {
                AbstractGameAction.AttackEffect.SLASH_HEAVY,
                AbstractGameAction.AttackEffect.FIRE,
                AbstractGameAction.AttackEffect.SLASH_DIAGONAL };
    }

    @Override
    public String getSpireHeartText() {
        return "烧焦心脏";
    }

    @Override
    public String getVampireText() {
        return Vampires.DESCRIPTIONS[0];
    }

    @Override
    public void applyStartOfCombatLogic() {
        super.applyStartOfCombatLogic();
        // 如果没有鼠符咒，变石像；一旦有，得力量
        if (!AbstractDungeon.player.hasRelic("fuZhouMod:RatTalisman")) {
            AbstractDungeon.actionManager.addToBottom(
                    new ApplyPowerAction(this, this, new basicmod.powers.ShenZhuStatuePower(this)));
        } else {
            AbstractDungeon.actionManager.addToBottom(
                    new ApplyPowerAction(this, this, new basicmod.powers.ShenZhuRevivedPower(this)));
        }
    }
}
