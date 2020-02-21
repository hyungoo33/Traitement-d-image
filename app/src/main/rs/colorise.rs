# pragma version (1)
# pragma rs java_package_name ( com.qps.renderscript )
# pragma rs fp relaxed

int hue;


static float4 hsv(uchar4 in)
{
    const float4 pixelf = rsUnpackColor8888 (in) ;
    float4 out;

    const float Cmin = min(pixelf.r ,min(pixelf.g ,pixelf.b));
    const float Cmax = max(pixelf.r ,max(pixelf.g ,pixelf.b));
    const float delta = Cmax - Cmin;

    if(delta <= 0){
        out.s0 = 0;
        out.s1 = 0;
    }else{
        out.s1 = delta/Cmax;
        if(Cmax == pixelf.r){
            out.s0 = 60*(fmod(((pixelf.g - pixelf.b)/delta), 6.f));
        }
        else if(Cmax == pixelf.g){
            out.s0 = 60*((pixelf.b - pixelf.r)/delta + 2);
        }
        else{
            out.s0 = 60*((pixelf.r - pixelf.g)/delta + 4);
        }
    }

    out.s2 = Cmax;

    out.s3 = pixelf.a;

    return out;

}


static uchar4 rgb(float4 in)
{
    const float C = in.s2 * in.s1;
    const float X = C *(1 - fabs(fmod((in.s0/60),2) - 1));
    const float m = in.s2 - C;

    float r = 0.0f;
    float g = 0.0f;
    float b = 0.0f;

    if(in.s0 >= 0 && in.s0 <60){
            r = C;
            g = X;
        }
        else if(in.s0 >=60 && in.s0 <120){
            r = X;
            g = C;
        }
        else if(in.s0 >=120 && in.s0 <180){
            b = X;
            g = C;
        }
        else if(in.s0 >=180 && in.s0 <240){
            g = X;
            b = C;
        }
        else if(in.s0 >=240 && in.s0 <300){
            b = X;
            r = C;
        }
        else if(in.s0 >=300 && in.s0 <360){
            r = X;
            b = C;
        }
    const float R = (r + m);
    const float G = (g + m);
    const float B = (b + m);

    return rsPackColorTo8888 (R ,G ,B , in.s3);
}


uchar4 RS_KERNEL colorise ( uchar4 in ) {
    float4 color = hsv(in);
    color.s0 = hue;
    uchar4 out = rgb(color);
    return out;

}