import os
from PIL import Image

# ==========================================
# 缩放单个遗物图标的脚本
# python d:\code\my\2\fuZhouMod\my_doc\resize_locator.py
# ==========================================

input_path = r"d:\code\my\2\fuZhouMod\my_doc\符咒定位仪.png"
output_dir = r"d:\code\my\2\fuZhouMod\src\main\resources\basicmod\images\relics"
large_output_dir = os.path.join(output_dir, "large")

if not os.path.exists(output_dir): os.makedirs(output_dir)
if not os.path.exists(large_output_dir): os.makedirs(large_output_dir)

name = "TalismanLocator"

try:
    img = Image.open(input_path).convert("RGBA")
    
    # 获取原始图像宽高
    cw, ch = img.size
    size = max(cw, ch)
    # 居中放到正方形透明图层里，防变形
    square = Image.new("RGBA", (size, size), (0,0,0,0))
    square.paste(img, ((size - cw) // 2, (size - ch) // 2))
    
    # 缩小到 84x84（占128的约65%-70%比例，与普通遗物相仿）
    inner_size = 84
    icon_128 = Image.new("RGBA", (128, 128), (0,0,0,0))
    shrunk_img = square.resize((inner_size, inner_size), Image.Resampling.LANCZOS)
    icon_128.paste(shrunk_img, ((128 - inner_size) // 2, (128 - inner_size) // 2))
    icon_128.save(os.path.join(output_dir, f"{name}.png"), "PNG")
    
    # 大图缩放到192并贴到256上去
    inner_large = 192
    icon_256 = Image.new("RGBA", (256, 256), (0,0,0,0))
    shrunk_large = square.resize((inner_large, inner_large), Image.Resampling.LANCZOS)
    icon_256.paste(shrunk_large, ((256 - inner_large) // 2, (256 - inner_large) // 2))
    icon_256.save(os.path.join(large_output_dir, f"{name}.png"), "PNG")

    print(f"✅ 处理完成！已生成普通图({name}.png)和大图(large/{name}.png)")
except Exception as e:
    print(f"❌ 执行出错啦: {e}")
