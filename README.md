This project is an Android app which could help people analyze the trend of the particular stocks the users choose and also offer a platform to practice trades. It has the home screen and the details screen. The back end is deployed on Azure.

For the home screen, it include Portfolio section and Favorite section, the stocks shown here will refresh every 15 seconds, supports by the [Tiingo](https://www.tiingo.com/) API. For items in the home screen, you could drag and drop the list and also swipe to delete one particular stock from the list. Besides that, it includes an auto-complete search button in the toolbar, whenever you search some stocks' ticker, you will get into the detail screen of that stock.



![image-20201209202237031](https://github.com/shuowang97/Android-Stock-Analyzer-and-Trader/blob/main/img-github/image-20201209202237031.png)



![image-20201209202220551](https://github.com/shuowang97/Android-Stock-Analyzer-and-Trader/blob/main/img-github/image-20201209202220551.png)



![image-20201209202405666](https://github.com/shuowang97/Android-Stock-Analyzer-and-Trader/blob/main/img-github/image-20201209202405666.png)



![image-20201209202420233](https://github.com/shuowang97/Android-Stock-Analyzer-and-Trader/blob/main/img-github/image-20201209202420233.png)



For the details screen, you can see a chart supported by HighCharts API, which shows the price history of the particular item you want to see for multiple choices, like 3 months, 6 months, 1 year, and 2 year. You will also see the Current price, low price, open price, bid price, high price and the volume for today.  Besides that, it also include some news for this company to help users get the newest information, supported by  [NewsAPI](https://newsapi.org/).



![image-20201209202449207](https://github.com/shuowang97/Android-Stock-Analyzer-and-Trader/blob/main/img-github/image-20201209202449207.png)



![image-20201209202944819](https://github.com/shuowang97/Android-Stock-Analyzer-and-Trader/blob/main/img-github/image-20201209202944819.png)



![image-20201209202605533](https://github.com/shuowang97/Android-Stock-Analyzer-and-Trader/blob/main/img-github/image-20201209202605533.png)



Functionality Demo Video: [CSCI 571 Fall 2020 - Homework #9 Android - YouTube](https://www.youtube.com/watch?v=VH63nyau-Nc&feature=youtu.be)



API: [Tiingo](https://www.tiingo.com/), [Twitter](https://developer.twitter.com/en/docs/twitter-for-websites/tweet-button/guides/web-intent), [News](https://newsapi.org/), [HighCharts](https://www.highcharts.com/)



Third Party Tools include: Volley, Google's Material, Picasso, Azure. 

