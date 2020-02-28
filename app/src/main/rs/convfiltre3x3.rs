# pragma version (1)
# pragma rs java_package_name ( com.qps.projettp1)
# pragma rs fp relaxed


float* filter;
int imageWidth;
int imageHeight;
float div;
bool align;


void conv(const uchar4* in, uchar4* out,uint32_t x, uint32_t y){



    float4 moy = 0;
    for(int i = - 1; i < 2; i++){
        for(int j = - 1; j < 2; j++){

            moy += rsUnpackColor8888(in[(int)fmod((float)i*imageWidth +j,(float)imageHeight*imageWidth)])*filter[(i+1)*imageWidth + (j+1)];
        }
    }
    if(div !=0.f ){
        moy /= div;
    }
    if(align){
        moy =(moy+1.f)/2.f;
    }
    moy.a = 1.0f;

    *out = rsPackColorTo8888(moy);


}
