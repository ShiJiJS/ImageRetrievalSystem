<template>
  <div>
    <div class="container">
        <article class="image-card">
            <img :src="image1_src" alt="">
        </article>
        <!-- annoInfos[showedPairId].imageURL1 -->
        <article class="image-card">
            <img :src="image2_src" alt="">
        </article>
        <article class="button-card">
            <button @click="handleButtonClick(true)">相似</button>
            <button @click="handleButtonClick(false)">不相似</button>
        </article>
    </div>
    <button class="cal-button" @click="handleCalButton">计算结果</button>
    
    <table>
        <tr><th>颜色矩计算结果</th></tr>
        <tr>
            <td>Accuracy</td>
            <td>{{this.calculateResult.accuracy_cm}}</td>
        </tr>
        <tr>
            <td>Precision</td>
            <td>{{this.calculateResult.precision_cm}}</td>
        </tr>
        <tr>
            <td>Recall</td>
            <td>{{this.calculateResult.recall_cm}}</td>
        </tr>
        <tr>
            <td>F-measure</td>
            <td>{{this.calculateResult.f_measure_cm}}</td>
        </tr>
    </table>

    <table>
        <tr><th>ORB计算结果</th></tr>
        <tr>
            <td>Accuracy</td>
            <td>{{this.calculateResult.accuracy_orb}}</td>
        </tr>
        <tr>
            <td>Precision</td>
            <td>{{this.calculateResult.precision_orb}}</td>
        </tr>
        <tr>
            <td>Recall</td>
            <td>{{this.calculateResult.recall_orb}}</td>
        </tr>
        <tr>
            <td>F-measure</td>
            <td>{{this.calculateResult.f_measure_orb}}</td>
        </tr>
    </table>
  </div>
</template>

<script>
import request from "../utils/request";
export default {
    mounted() {
        this.getAnnoInfos();
    },
    methods: {
        getAnnoInfos() {
          request({
            url: "/annoInfo/2000/1",
            method: "get",
          }).then((response) => {
            this.annoInfos = response.data.data;
            this.image1_src = this.annoInfos[this.showedPairIndex].imageURL1;
            this.image2_src = this.annoInfos[this.showedPairIndex].imageURL2;
          });
        },
        handleButtonClick(isSimilar){
            var data = { pairId: this.annoInfos[this.showedPairIndex].pairId,isSimilar:isSimilar};
            request({
              url: "/humanAnno",
              method: "post",
              headers: {
                "Content-Type": "application/json",
              },
              data: JSON.stringify(data),
            }).then((response) => {
              console.log(response.data);
            });
            
            if((this.showedPairIndex + 1) < this.annoInfos.length){
                this.showedPairIndex ++;
                this.image1_src = this.annoInfos[this.showedPairIndex].imageURL1;
                this.image2_src = this.annoInfos[this.showedPairIndex].imageURL2;
            }else{
                alert("标注完成，请点击计算按钮");
            }
        },
        handleCalButton(){
            request({
            url: "/calculate",
            method: "get",
          }).then((response) => {
            this.calculateResult = response.data.data
            console.log(response.data)
          });
        }
    },

    data() {
        return {
          image1_src:"",
          image2_src:"",
          annoInfos: [],
          showedPairIndex:0,
          calculateResult:{}
        };
    },

}
</script>

<style scoped>
*{
    margin:0;
}
.container{
    height: 500px;
}
.button-card{
  width: 20%;
  float: left;
  display: inline-block;
  height: 500px
  
}
.image-card{
    width:40%;
    height: 500px;
    display: inline-block;
    float: left;
}
button {
    margin-top: 70px;
}
.cal-button{
    margin-top: 30px;
}
img{
    height: 100%;
    width: 100%;
}
table{margin-top: 50px;}
</style>