fonctions expérimentés sur un asus zenfone 5 avec Android 9 API 28

La modification d'image se fait dans le MainActivity ligne 23.

pour les fonctions dans le menu :- toGrayFast/colorize/isolateColor je conseille l'image : R.id.isolate
                                 - contrastAugment/contrastDecrease   :  R.id.showcontrast
                                 - contrastAugmentR/G/B/H/S/V : R.id.contrastcolor
                                 - equalizate : R.id.showcontrast
                                 - equalizateR/G/B/H/S/V : R.id.egalise
                                 - moyenneur/gauss5 : R.id.puppet
                                 - detectHoriP/S detectVertP/S laplace4/8 : R.id.convolution
                                 
Il est possible de modifier la taille du filtre moyenneur en modifier la valeur de n à la ligne 1175 , le filtre sera alors de taille : 2*n - 1 * 2*n - 1 .                           
