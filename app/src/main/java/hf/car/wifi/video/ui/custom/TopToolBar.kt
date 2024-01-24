package hf.car.wifi.video.ui.custom

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import hf.car.wifi.video.R
import hf.car.wifi.video.databinding.ViewTopToolBinding

class TopToolBar :FrameLayout{

    private val binding: ViewTopToolBinding =
        ViewTopToolBinding.inflate(LayoutInflater.from(context), this, true)

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : this(
        context, attrs, defStyleAttr, 0
    )

    constructor(
        context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        initView(context, attrs)
    }

    fun setBackClickListener(onClickListener: OnClickListener) {
        binding.imageBack.setOnClickListener(onClickListener)
    }

    fun setTitleContent(title: String) {
        binding.tvTitle.text = title
    }

    fun setMoreTitleContent(title: String) {
        binding.tvMore.text = title
    }

    fun setMoreClickListener(onClickListener: OnClickListener) {
        binding.tvMore.setOnClickListener(onClickListener)
        binding.imageMore.setOnClickListener(onClickListener)
    }

    private fun initView(context: Context, attrs: AttributeSet?) {
        val typedArray: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.TopToolBar)

        val leftIconRes =
            typedArray.getResourceId(R.styleable.TopToolBar_left_icon, R.drawable.icon_return)
        val leftIconVisible =
            typedArray.getBoolean(R.styleable.TopToolBar_left_icon_visible, true)

        val centerTitleStr = typedArray.getString(R.styleable.TopToolBar_center_title)
        val centerTitleColor =
            typedArray.getResourceId(R.styleable.TopToolBar_center_title_color, R.color.color_333333)
        val centerTitleSize =
            typedArray.getResourceId(R.styleable.TopToolBar_center_title_size, R.dimen.sp16)

        val rightIconRes =
            typedArray.getResourceId(R.styleable.TopToolBar_right_icon, R.drawable.icon_return)
        val rightIconVisible =
            typedArray.getBoolean(R.styleable.TopToolBar_right_icon_visible, false)

        val rightTitleStr = typedArray.getString(R.styleable.TopToolBar_center_title)
        val rightTitleColor =
            typedArray.getResourceId(R.styleable.TopToolBar_right_title_color, R.color.color_333333)
        val rightTitleSize =
            typedArray.getResourceId(R.styleable.TopToolBar_right_title_size, R.dimen.sp16)

        typedArray.recycle()

        binding.imageBack.setImageResource(leftIconRes)
        if (leftIconVisible) {
            binding.imageBack.visibility = VISIBLE
        } else {
            binding.imageBack.visibility = GONE
        }

        binding.tvTitle.text = centerTitleStr
        binding.tvTitle.setTextColor(ContextCompat.getColor(context, centerTitleColor))
        binding.tvTitle.setTextSize(
            TypedValue.COMPLEX_UNIT_PX,
            resources.getDimension(centerTitleSize)
        )

        binding.imageMore.setImageResource(rightIconRes)
        if (rightIconVisible) {
            binding.imageMore.visibility = VISIBLE
        } else {
            binding.imageMore.visibility = GONE
        }

        binding.tvMore.text = rightTitleStr
        binding.tvMore.setTextColor(ContextCompat.getColor(context, rightTitleColor))
        binding.tvMore.setTextSize(
            TypedValue.COMPLEX_UNIT_PX,
            resources.getDimension(rightTitleSize)
        )
    }
}