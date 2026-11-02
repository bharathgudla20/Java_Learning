import {writeFileSync} from 'node:fs';
const ref=name=>({$ref:`#/components/schemas/${name}`});
const uuid={type:'string',format:'uuid'}, str={type:'string'}, integer={type:'integer'}, amount={type:'number',multipleOf:.01};
const obj=(properties,required=Object.keys(properties))=>({type:'object',properties,required});
const schemas={
  Csrf:obj({token:str,headerName:str}),
  Account:obj({id:uuid,name:str,demo:{type:'boolean'},ephemeral:{type:'boolean'}}),
  Register:obj({name:{type:'string',minLength:1,maxLength:60},email:{type:'string',format:'email'},password:{type:'string',minLength:10,maxLength:64,description:'Maximum 72 UTF-8 bytes'}}),
  Login:obj({email:{type:'string',format:'email'},password:str}),
  ProductInput:obj({sku:{type:'string',pattern:'^[A-Za-z0-9-]{2,32}$'},name:{type:'string',minLength:1,maxLength:100},category:{type:'string',minLength:1,maxLength:40},price:{...amount,minimum:.01,maximum:9999999.99},stock:{...integer,minimum:0,maximum:1000000},reorderPoint:{...integer,minimum:0,maximum:100000}}),
  Product:obj({id:uuid,sku:str,name:str,category:str,price:amount,stock:integer,reorderPoint:integer,version:integer}),
  StockInput:obj({change:{...integer,minimum:-1000000,maximum:1000000,description:'Non-zero delta. Resulting stock must be between 0 and 1,000,000.'},reason:{type:'string',minLength:1,maxLength:120}}),
  LineInput:obj({productId:uuid,quantity:{...integer,minimum:1,maximum:10000}}),
  OrderInput:obj({customer:{type:'string',minLength:1,maxLength:100},email:{type:'string',format:'email',maxLength:254},lines:{type:'array',minItems:1,maxItems:30,items:ref('LineInput')}}),
  OrderLine:obj({productId:uuid,name:str,sku:str,quantity:integer,unitPrice:amount}),
  Order:obj({id:uuid,reference:str,customer:str,email:str,status:{type:'string',enum:['PLACED','SHIPPED','CANCELLED']},total:amount,createdAt:{type:'string',format:'date-time'},lines:{type:'array',items:ref('OrderLine')}}),
  StatusInput:obj({status:{type:'string',enum:['SHIPPED','CANCELLED']}}),
  Activity:obj({kind:str,message:str,createdAt:{type:'string',format:'date-time'}}),
  Day:obj({date:{type:'string',format:'date'},revenue:amount,orders:integer}),
  Overview:obj({revenue:amount,inventoryValue:amount,activeProducts:integer,lowStock:integer,orders:integer,pendingOrders:integer,trend:{type:'array',items:ref('Day')},stockAlerts:{type:'array',items:ref('Product')},recentOrders:{type:'array',items:ref('Order')},activity:{type:'array',items:ref('Activity')}}),
  ProductPage:obj({content:{type:'array',items:ref('Product')},page:integer,totalPages:integer,totalElements:integer}),
  Problem:obj({status:integer,detail:str,title:str,fields:{type:'object',additionalProperties:str}},['detail'])
};
const paths={};
function endpoint(path,method,summary,{input,output,status=200,publicAccess=false,parameters=[],description=''}={}){
  const responseSchema=typeof output==='string'?ref(output):output;
  const op={summary,description,security:publicAccess?[]:[{session:[]}],parameters:[...parameters],responses:{[status]:{description:'Success',...(output?{content:{'application/json':{schema:responseSchema}}}:{})}}};
  for(const code of [400,401,403,409,429])op.responses[code]={description:({400:'Invalid input',401:'Authentication required',403:'CSRF or access denied',409:'Business conflict',429:'Rate limited'})[code],content:{'application/json':{schema:ref('Problem')}}};
  if(!['get','head'].includes(method))op.parameters.push({name:'X-CSRF-TOKEN',in:'header',required:true,schema:str,description:'Obtain the current masked token from /api/auth/csrf.'});
  if(input)op.requestBody={required:true,content:{'application/json':{schema:ref(input)}}};
  paths[path]??={};paths[path][method]=op;
}
endpoint('/api/auth/csrf','get','Obtain a CSRF token',{output:'Csrf',publicAccess:true});
endpoint('/api/auth/me','get','Current account',{output:'Account'});
endpoint('/api/auth/demo','post','Create isolated sample workspace',{output:'Account',publicAccess:true});
endpoint('/api/auth/register','post','Create account and session',{input:'Register',output:'Account',status:201,publicAccess:true});
endpoint('/api/auth/login','post','Sign in',{input:'Login',output:'Account',publicAccess:true});
endpoint('/api/auth/logout','post','Invalidate session',{status:204});
endpoint('/api/overview','get','Calculated workspace dashboard',{output:'Overview'});
endpoint('/api/products','get','Search active products',{output:'ProductPage',parameters:[{name:'q',in:'query',schema:str},{name:'category',in:'query',schema:str},{name:'low',in:'query',schema:{type:'boolean',default:false}},{name:'page',in:'query',schema:{type:'integer',minimum:0,maximum:10000,default:0}}],description:'Eight results per page, sorted by name then id. All queries are scoped to the current owner.'});
endpoint('/api/catalog','get','Active products for order selection',{output:{type:'array',items:ref('Product')}});
endpoint('/api/products','post','Create product',{input:'ProductInput',output:'Product',status:201});
const id={name:'id',in:'path',required:true,schema:uuid};
endpoint('/api/products/{id}/stock','patch','Adjust stock',{parameters:[id],input:'StockInput',output:'Product'});
endpoint('/api/products/{id}','delete','Archive product',{parameters:[id],status:204});
endpoint('/api/orders','get','List workspace orders, newest first',{output:{type:'array',items:ref('Order')},description:'Workspace capped at 300 orders. Totals are order values, not captured payments.'});
endpoint('/api/orders','post','Place atomic, idempotent order',{input:'OrderInput',output:'Order',status:201,parameters:[{name:'Idempotency-Key',in:'header',required:true,schema:{type:'string',pattern:'^[A-Za-z0-9-]{8,80}$'}}],description:'Same key and input return the original order; changed input with an existing key returns 409. Prices come from the server.'});
endpoint('/api/orders/{id}/status','patch','Ship or cancel a placed order',{parameters:[id],input:'StatusInput',output:'Order',description:'Cancellation restores stock exactly once. Repeating the current status is a no-op.'});
endpoint('/api/demo/reset','post','Replace current demo dataset with sample data',{status:204});
const spec={openapi:'3.0.3',info:{title:'Stockroom API',version:'1.0.0',description:'Java Spring Boot portfolio project by Bharath Gudla. Same-origin session authentication and CSRF protection. Temporary demo data resets on hosted server restart. INR prices, UTC trend dates.'},servers:[{url:'/'}],paths,components:{securitySchemes:{session:{type:'apiKey',in:'cookie',name:'JSESSIONID',description:'HttpOnly session established by demo/register/login.'}},schemas}};
writeFileSync(new URL('../src/main/resources/static/api-docs.json',import.meta.url),JSON.stringify(spec,null,2)+'\n');
console.log('OpenAPI specification generated.');
