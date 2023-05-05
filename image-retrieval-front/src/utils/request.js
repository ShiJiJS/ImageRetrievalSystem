import axios from "axios";
import requestConst from "../const/requestConst";

var request = axios.create({
  baseURL: requestConst.baseURL,
});

export default request;
