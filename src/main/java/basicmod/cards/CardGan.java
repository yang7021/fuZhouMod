package basicmod.cards;

import basicmod.util.CardStats;
import basicmod.enums.CharacterEnums;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.combat.FlashAtkImgEffect;

public class CardGan extends BaseCard {
    public static final String ID = makeID("Gan");
    private static final CardStats info = new CardStats(
            CharacterEnums.SHENGZHU_COLOR,
            CardType.ATTACK,
            CardRarity.SPECIAL,
            CardTarget.ENEMY,
            1);

    public CardGan() {
        super(ID, info);
        setDamage(8, 4); // 8 -> 12
        setMagic(0); // 记录暴击增长阶段
        this.misc = upgraded ? 20 : 10; // 可以用来做动态描述的初始暴击率，但不强求，用 magic即可
    }

    @Override
    public void upgrade() {
        super.upgrade();
        // 如果需要动态改变初始暴击率
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        int finalDamage = this.damage;
        
        // 暴击判定：基础暴击率 + 打出次数*10
        int baseCrit = upgraded ? 20 : 10;
        int currentCrit = baseCrit + this.magicNumber * 10;
        
        int rng = AbstractDungeon.cardRandomRng.random(1, 100);
        
        if (rng <= currentCrit) {
            // 触发暴击
            finalDamage = (int)(finalDamage * 1.5f);
            AbstractDungeon.effectList.add(new FlashAtkImgEffect(m.hb.cX, m.hb.cY, AbstractGameAction.AttackEffect.FIRE, false));
        }
        
        addToBot(new DamageAction(m, new DamageInfo(p, finalDamage, DamageInfo.DamageType.NORMAL), AbstractGameAction.AttackEffect.SLASH_HEAVY));
        
        // 增加使用次数记录
        addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                magicNumber++;
                isMagicNumberModified = true;
                this.isDone = true;
            }
        });
    }
}
