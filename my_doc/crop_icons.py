import os
from PIL import Image

# ==========================================
# 老大爷，按您的原图实际情况修改下面的数值
# ==========================================

# 1. 您的合照图片路径
img_path = r"d:\code\my\2\fuZhouMod\my_doc\十二符咒合计图.png"

# 2. 图片里符咒的排版（几行几列）
rows = 3
cols = 4

# 3. 裁剪收缩边距（如果您发现切出来还是带一点旁边的边，把这个值调大，比如 2、5、甚至 10）
shrink_margin = 0 

# 4. 符咒在图里从左到右、从上到下的顺序
talisman_names = [
    "RatTalisman",      # 鼠
    "OxTalisman",       # 牛
    "RabbitTalisman",   # 兔
    "TigerTalisman",    # 虎
	"HorseTalisman",    # 马
	"SheepTalisman",    # 羊
    "DragonTalisman",   # 龙
    "SnakeTalisman",    # 蛇
    "MonkeyTalisman",   # 猴
    "RoosterTalisman",  # 鸡
    "DogTalisman",      # 狗
    "PigTalisman"       # 猪
]

# ==========================================
# 智能裁剪核心代码（不用改）
# ==========================================

output_dir = r"d:\code\my\2\fuZhouMod\src\main\resources\basicmod\images\relics"
large_output_dir = os.path.join(output_dir, "large")

if not os.path.exists(output_dir): os.makedirs(output_dir)
if not os.path.exists(large_output_dir): os.makedirs(large_output_dir)

try:
    img = Image.open(img_path).convert("RGBA")
    width, height = img.size
    pixels = img.load()
    
    # 智能过滤：用左上角的像素当背景色（不管是纯色或透明都能识别）
    bg_color = pixels[0, 0]
    def is_bg(x, y):
        r, g, b, a = pixels[x, y]
        if a < 10: return True
        return abs(r - bg_color[0]) < 10 and abs(g - bg_color[1]) < 10 and abs(b - bg_color[2]) < 10

    # 扫描整图，记录哪一行、哪一列有内容
    row_has_content = [False] * height
    col_has_content = [False] * width
    for y in range(height):
        for x in range(width):
            if not is_bg(x, y):
                row_has_content[y] = True
                col_has_content[x] = True

    # 根据扫描结果，找到每个图标所在的块段
    def get_segments(has_content_list, min_gap=5):
        segments = []
        start = 0
        in_content = False
        empty_count = 0
        
        for i, val in enumerate(has_content_list):
            if val:
                if not in_content:
                    start = i
                    in_content = True
                empty_count = 0
            else:
                if in_content:
                    empty_count += 1
                    if empty_count >= min_gap: # 连续多少个空白才算缝隙
                        segments.append((start, i - empty_count))
                        in_content = False
                        
        if in_content:
            segments.append((start, len(has_content_list)))
        return segments

    y_segments = get_segments(row_has_content)
    x_segments = get_segments(col_has_content)
    
    # 检测：如果智能识别出正好 3 行 4 列，则直接用精确边界
    if len(y_segments) == rows and len(x_segments) == cols:
        print("✅ 智能识别生效：已完美扫描出12个独立图标的绝对坐标！")
        count = 0
        for ys in y_segments:
            for xs in x_segments:
                if count >= len(talisman_names): break
                left = xs[0] + shrink_margin
                upper = ys[0] + shrink_margin
                right = xs[1] - shrink_margin
                lower = ys[1] - shrink_margin
                
                # 裁剪
                cropped = img.crop((left, upper, right, lower))
                
                # 把切下来的图居中放在一个正透明图层里，防变形
                cw, ch = cropped.size
                size = max(cw, ch)
                square = Image.new("RGBA", (size, size), (0,0,0,0))
                square.paste(cropped, ((size - cw) // 2, (size - ch) // 2))
                
                name = talisman_names[count]
                
                # 等比缩放到 84x84（大概占70%），然后再贴回一张 128x128 的透明底图上
                inner_size = 84
                icon_128 = Image.new("RGBA", (128, 128), (0,0,0,0))
                shrunk_img = square.resize((inner_size, inner_size), Image.Resampling.LANCZOS)
                icon_128.paste(shrunk_img, ((128 - inner_size) // 2, (128 - inner_size) // 2))
                icon_128.save(os.path.join(output_dir, f"{name}.png"), "PNG")
                
                # 高清大图同理，缩到192贴到256上去
                inner_large = 192
                icon_256 = Image.new("RGBA", (256, 256), (0,0,0,0))
                shrunk_large = square.resize((inner_large, inner_large), Image.Resampling.LANCZOS)
                icon_256.paste(shrunk_large, ((256 - inner_large) // 2, (256 - inner_large) // 2))
                icon_256.save(os.path.join(large_output_dir, f"{name}.png"), "PNG")
                
                print(f"[{count+1}/12] 成功智能裁剪并缩小尺寸: {name}.png")
                count += 1
    else:
        # 回退模式：识别不到清晰的3行4列，改用计算大图总外框，再等分网格
        print(f"⚠️ 无法精确分隔(检测到{len(y_segments)}行{len(x_segments)}列)，切换为中心网格等分模式。")
        
        # 去掉大图外圈多余边距
        def find_global_bounds(content_list):
            first = next((i for i, x in enumerate(content_list) if x), 0)
            last = len(content_list) - next((i for i, x in enumerate(content_list[::-1]) if x), 0)
            return first, last
            
        top, bottom = find_global_bounds(row_has_content)
        left_edge, right_edge = find_global_bounds(col_has_content)
        
        # 裁剪出真正的整体有效区域
        trimmed_img = img.crop((left_edge, top, right_edge, bottom))
        w, h = trimmed_img.size
        
        item_w = w / cols
        item_h = h / rows
        
        count = 0
        for r in range(rows):
            for c in range(cols):
                if count >= len(talisman_names): break
                left = int(c * item_w) + shrink_margin
                upper = int(r * item_h) + shrink_margin
                right = int((c + 1) * item_w) - shrink_margin
                lower = int((r + 1) * item_h) - shrink_margin
                
                cropped = trimmed_img.crop((left, upper, right, lower))
                # 把切下来的图居中放在一个正透明图层里，防变形，并且按比例缩小
                cw, ch = cropped.size
                size = max(cw, ch)
                square = Image.new("RGBA", (size, size), (0,0,0,0))
                square.paste(cropped, ((size - cw) // 2, (size - ch) // 2))
                
                name = talisman_names[count]
                
                # Slay the Spire的普通遗物原图虽然是 128*128，但中间实体通常只有 76*76 到 84*84 左右，周围有一大圈透明留白。
                # 所以在这里我们强行缩小实体比例！我们把它先等比缩放到 84x84（大概占70%），然后再贴回一张 128x128 的透明底图上
                inner_size = 84
                icon_128 = Image.new("RGBA", (128, 128), (0,0,0,0))
                shrunk_img = square.resize((inner_size, inner_size), Image.Resampling.LANCZOS)
                icon_128.paste(shrunk_img, ((128 - inner_size) // 2, (128 - inner_size) // 2))
                icon_128.save(os.path.join(output_dir, f"{name}.png"), "PNG")
                
                # 高清大图同理，虽然是 256*256 但是稍微留白一点看着舒服
                inner_large = 192
                icon_256 = Image.new("RGBA", (256, 256), (0,0,0,0))
                shrunk_large = square.resize((inner_large, inner_large), Image.Resampling.LANCZOS)
                icon_256.paste(shrunk_large, ((256 - inner_large) // 2, (256 - inner_large) // 2))
                icon_256.save(os.path.join(large_output_dir, f"{name}.png"), "PNG")
                
                print(f"[{count+1}/12] 成功修正裁剪并缩小尺寸: {name}.png")
                count += 1

    print("\n✅ 更新版裁剪完成！老大爷再去看看现在符咒大小是不是和原版遗物差不多了！")
    
except Exception as e:
    print(f"\n❌ 执行出错啦: {e}")
