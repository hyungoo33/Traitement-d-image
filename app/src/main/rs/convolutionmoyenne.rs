# pragma version (1)
# pragma rs java_package_name ( com.qps.projettp1)
# pragma rs fp relaxed



int imageWidth;
int imageHeight;

int filterSize = 3;


void moyConv(const uchar4* in, uchar4* out,uint32_t x, uint32_t y){


    float4 moy = 0;
    int range = (filterSize - 1)/2;
    for(int i = - range; i < range + 1; i++){
        for(int j = - range; j < range + 1; j++){

            moy += rsUnpackColor8888(in[(int)fmod((float)i*imageWidth +j,(float)imageHeight*imageWidth)]);
        }
    }
    moy /= filterSize*filterSize;
    moy.a = 1.0f;

    *out = rsPackColorTo8888(moy);


}
