# Intra-View Design System

## 🎨 Color Palette

### Light Mode
```
Primary Colors:
- Primary Blue:      #0F52BA (Sapphire blue - elegant and trustworthy)
- Primary Dark:      #0A3D8F (Darker for contrast)
- Primary Light:     #4A7FD5 (Lighter for hover states)

Secondary Colors:
- Teal Green:        #06D6A0 (Modern, fresh)
- Warm Amber:        #FFB800 (Accent)

Neutral Colors:
- Background:        #FAFAFA (Soft white)
- Surface:           #FFFFFF (Pure white)
- Surface Elevated:  #F5F7FA (Subtle tint)
- Divider:           #E8ECF1 (Subtle lines)

Text Colors:
- Primary:           #1A1D29 (Almost black)
- Secondary:         #6B7280 (Muted gray)
- Tertiary:          #9CA3AF (Light gray)

Status Colors:
- Error:             #DC2626 (Clear, not harsh)
- Success:           #059669 (Fresh green)
- Warning:           #F59E0B (Attention-grabbing)
```

### Dark Mode
```
Background:          #0F1419 (Deep charcoal)
Surface:             #1A1F29 (Elevated dark)
Surface Elevated:    #242B38 (Higher elevation)
```

## 📏 Typography Scale

```
Display Large:       36sp / 44sp line / -0.5sp letter / Bold
Display Medium:      32sp / 40sp line / -0.25sp letter / Bold
Display Small:       28sp / 36sp line / 0sp letter / SemiBold

Headline Large:      24sp / 32sp line / 0sp letter / SemiBold
Headline Medium:     22sp / 30sp line / 0sp letter / SemiBold
Headline Small:      20sp / 28sp line / 0sp letter / SemiBold

Title Large:         18sp / 26sp line / 0sp letter / SemiBold
Title Medium:        16sp / 24sp line / 0.1sp letter / Medium
Title Small:         14sp / 20sp line / 0.1sp letter / Medium

Body Large:          16sp / 26sp line / 0.15sp letter / Normal
Body Medium:         14sp / 22sp line / 0.15sp letter / Normal
Body Small:          12sp / 18sp line / 0.2sp letter / Normal

Label Large:         15sp / 20sp line / 0.1sp letter / Medium
Label Medium:        12sp / 18sp line / 0.4sp letter / Medium
Label Small:         11sp / 16sp line / 0.5sp letter / Medium
```

## 📐 Spacing System (8dp Grid)

```
Extra Small:    4dp   (tight spacing)
Small:          8dp   (minimal spacing)
Medium:         12dp  (compact spacing)
Normal:         16dp  (standard spacing)
Large:          20dp  (generous spacing)
Extra Large:    24dp  (section spacing)
Huge:           32dp  (major sections)
Massive:        40dp  (screen sections)
Gigantic:       48dp  (large gaps)
```

## 🎯 Component Dimensions

### Buttons
```
Small:          36dp height
Medium:         48dp height (standard)
Large:          56dp height (primary actions)

Corner Radius:  
- Small:        8dp
- Medium:       12dp
- Large:        16dp
```

### Icons
```
Small:          16dp
Medium:         24dp (standard)
Large:          32dp
Extra Large:    48dp
```

### Cards
```
Elevation:
- None:         0dp
- Low:          1dp
- Medium:       4dp (standard)
- High:         8dp
- Extra High:   16dp

Corner Radius:
- Small:        8dp
- Medium:       12dp (standard)
- Large:        16dp
- Extra Large:  24dp
- Full:         999dp (pills/badges)
```

## ⚡ Animation Specifications

### Durations
```
Fast:           150ms  (micro-interactions)
Normal:         300ms  (standard transitions)
Slow:           500ms  (complex animations)
Extra Slow:     700ms  (dramatic effects)
```

### Easing Functions
```
Standard:       cubic-bezier(0.4, 0.0, 0.2, 1.0)
Decelerate:     cubic-bezier(0.0, 0.0, 0.2, 1.0)
Accelerate:     cubic-bezier(0.4, 0.0, 1.0, 1.0)
Emphasized:     cubic-bezier(0.2, 0.0, 0.0, 1.0)
Emphasized Dec: cubic-bezier(0.05, 0.7, 0.1, 1.0)
```

### Spring Physics
```
Default:        DampingRatio.MediumBouncy + Stiffness.Low
Gentle:         DampingRatio.LowBouncy + Stiffness.VeryLow
Bouncy:         DampingRatio.MediumBouncy + Stiffness.Medium
```

## 🎭 Animation Patterns

### Press Animations
- Scale down to 0.95-0.97x
- Spring bounce back
- Duration: ~300ms

### Card Entry
- Fade in + slide up from 25% offset
- Staggered delay: 50ms per item (max 300ms)
- Duration: 400ms

### Loading States
- Pulsing scale: 0.95x to 1.05x
- Pulsing alpha: 0.6 to 1.0
- Infinite loop with reverse

### Dialogs/Modals
- Fade in + scale from 0.8x
- Emphasized decelerate easing
- Duration: 300ms

### Empty States
- Floating icon: -8dp to +8dp vertical
- Pulsing icon alpha: 0.4 to 0.6
- Duration: 2000ms

### Error States
- Subtle shake: -2dp to +2dp horizontal
- Fast linear easing
- Duration: 100ms per cycle

## 📱 Screen Patterns

### List Screens
```
Content Padding:     16dp
Item Spacing:        12-16dp
Card Elevation:      4dp
Entry Animation:     Staggered fade + slide
```

### Detail Screens
```
Header Height:       200-220dp
Content Padding:     16dp
Section Spacing:     32dp
Header Icons:        24dp with 8dp spacing
```

### Form Screens
```
Field Spacing:       16dp
Field Corner:        12dp
Button Height:       56dp
Button Corner:       16dp
Leading Icon:        24dp
Error Surface:       12dp corner radius
```

### Dialog/Modal
```
Corner Radius:       16-20dp
Content Padding:     24dp
Button Height:       48dp
Max Width:           560dp (tablets)
```

## 🎨 UI Component Specifications

### Event Card
```
Height:              220dp
Corner Radius:       16dp
Elevation:           4dp (pressed: 1dp)
Gradient:            Transparent → Black 70%
Text Padding:        16dp
Press Scale:         0.97x
```

### Photo Grid
```
Columns:             3 fixed
Item Spacing:        4dp
Aspect Ratio:        1:1
Corner Radius:       8dp
Entry Delay:         30ms per item
Entry Scale:         0.8x → 1.0x
```

### QR Display
```
Card Size:           300dp
Corner Radius:       24dp
Elevation:           8dp
Card Padding:        24dp
Pulse Scale:         0.98x → 1.02x
Badge Radius:        999dp (full pill)
```

### Loading Indicator
```
Size:                48dp
Stroke Width:        3dp
Scale Animation:     0.95x → 1.05x
Alpha Animation:     0.6 → 1.0
Duration:            1000ms reverse
```

## 🔄 State Management

### Interactive States
```
Default:             100% opacity
Hover:               95% opacity (desktop)
Pressed:             Scale 0.95-0.97x
Disabled:            38% opacity
Focus:               Outline visible
```

### Loading States
```
Initial:             Skeleton/shimmer
Loading:             Progress indicator + text
Success:             Fade to content
Error:               Error state component
```

### Empty States
```
Icon Size:           120dp (48dp × 2.5)
Icon Background:     40% opacity circle
Icon Animation:      Floating + pulsing
Message:             Title medium typography
```

## 🎯 Best Practices

### DO's
✅ Use AppSpacing for all spacing
✅ Use AppDimensions for all sizes
✅ Apply animations for state changes
✅ Provide visual feedback for interactions
✅ Use spring animations for natural feel
✅ Stagger list/grid animations
✅ Maintain minimum 48dp touch targets
✅ Use outlined icons for modern look
✅ Apply proper elevation hierarchy

### DON'Ts
❌ Use hardcoded dp values
❌ Skip animations on state changes
❌ Overuse animations (keep subtle)
❌ Use filled icons everywhere
❌ Ignore loading/error states
❌ Forget press feedback
❌ Use inconsistent corner radius
❌ Apply heavy shadows everywhere

## 📊 Accessibility

### Color Contrast
- Text on background: 4.5:1 minimum
- Large text: 3:1 minimum
- Icons: 3:1 minimum

### Touch Targets
- Minimum: 48dp × 48dp
- Recommended: 56dp × 56dp for primary actions

### Motion
- Respect system animation settings
- Provide reduced motion alternatives
- Keep animations under 500ms for critical paths

## 🚀 Implementation

All design tokens are implemented in:
- `ui/theme/Color.kt` - Color palette
- `ui/theme/Type.kt` - Typography scale
- `ui/theme/Theme.kt` - Theme configuration
- `ui/theme/Spacing.kt` - Spacing system
- `ui/theme/Animation.kt` - Animation utilities

Use these consistently across all screens and components for a cohesive experience.

