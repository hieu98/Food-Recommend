#!F:/Python/Python39/python.exe
from flask import Flask, render_template, request
import pandas as pd 
import numpy as np
import json
from sklearn.metrics.pairwise import cosine_similarity
from scipy import sparse 

app = Flask(__name__)

@app.route('/', methods=['POST'])
def predict():
    uid = request.form['uid'] 
    data = request.form['data']
    print(uid)
    if uid == "null":
        return ("a")
    else :
        if data == "1":
            return ("a")
        else :
            with open('ex.txt', 'w') as f:
                f.write(str(data))
            class CF(object):
                def __init__(self, Y_data, k, dist_func = cosine_similarity, uuCF = 1):
                    self.uuCF = uuCF 
                    self.Y_data = Y_data if uuCF else Y_data[:, [1, 0, 2]]
                    self.k = k
                    self.dist_func = dist_func
                    self.Ybar_data = None
                    self.n_users = int(np.max(self.Y_data[:, 0])) + 1 
                    self.n_items = int(np.max(self.Y_data[:, 1])) + 1
                
                def add(self, new_data):
                    self.Y_data = np.concatenate((self.Y_data, new_data), axis = 0)
                
                def normalize_Y(self):
                    users = self.Y_data[:, 0] 
                    self.Ybar_data = self.Y_data.copy()
                    self.mu = np.zeros((self.n_users,))
                    for n in range(self.n_users):
                        ids = np.where(users == n)[0].astype(np.int32)
                        item_ids = self.Y_data[ids, 1] 
                        ratings = self.Y_data[ids, 2]
                        
                        m = np.mean(ratings) 
                        if np.isnan(m):
                            m = 0 
                        self.mu[n] = m
                        
                        self.Ybar_data[ids, 2] = ratings - self.mu[n]

                    self.Ybar = sparse.coo_matrix((self.Ybar_data[:, 2],
                        (self.Ybar_data[:, 1], self.Ybar_data[:, 0])), (self.n_items, self.n_users))
                    self.Ybar = self.Ybar.tocsr()

                def similarity(self):
                    eps = 1e-6
                    self.S = self.dist_func(self.Ybar.T, self.Ybar.T)
                
                    
                def refresh(self):
                    self.normalize_Y()
                    self.similarity() 
                    
                def fit(self):
                    self.refresh()
                    
                
                def __pred(self, u, i, normalized = 1):

                    ids = np.where(self.Y_data[:, 1] == i)[0].astype(np.int32)
                    users_rated_i = (self.Y_data[ids, 0]).astype(np.int32)
                    sim = self.S[u, users_rated_i]
                    a = np.argsort(sim)[-self.k:] 
                    nearest_s = sim[a]
                    r = self.Ybar[i, users_rated_i[a]]
                    if normalized:
                        return (r*nearest_s)[0]/(np.abs(nearest_s).sum() + 1e-8)

                    return (r*nearest_s)[0]/(np.abs(nearest_s).sum() + 1e-8) + self.mu[u]
                
                def pred(self, u, i, normalized = 1):
                    if self.uuCF: return self.__pred(u, i, normalized)
                    return self.__pred(i, u, normalized)
                        
                
                def recommend(self, u):
                    ids = np.where(self.Y_data[:, 0] == u)[0]
                    items_rated_by_u = self.Y_data[ids, 1].tolist()              
                    recommended_items = []
                    for i in range(self.n_items):
                        if i not in items_rated_by_u:
                            rating = self.__pred(u, i)
                            if rating > 0: 
                                recommended_items.append(i)
                    
                    return recommended_items 
                
                def recommend2(self, u):
                    """
                    Determine all items should be recommended for user u.
                    The decision is made based on all i such that:
                    self.pred(u, i) > 0. Suppose we are considering items which 
                    have not been rated by u yet. 
                    """
                    ids = np.where(self.Y_data[:, 0] == u)[0]
                    items_rated_by_u = self.Y_data[ids, 1].tolist()              
                    recommended_items = []
                
                    for i in range(self.n_items):
                        if i not in items_rated_by_u:
                            rating = self.__pred(u, i)
                            if rating > 0: 
                                recommended_items.append(i)
                    
                    return recommended_items 

                def print_recommendation(self):
                    print ('Recommendation: ')
                    for u in range(self.n_users):
                        recommended_items = self.recommend(u)
                        if self.uuCF:
                            print ('    Recommend item(s):', recommended_items, 'for user', u)
                        else: 
                            print ('    Recommend item', u, 'for user(s) : ', recommended_items)

                #new
                def print_recommendation1(self):
                    """
                    print all items which should be recommended for each user 
                    """
                    print ('Recommendation: ')
                    
                    for u in range(self.n_users):
                        recommended_items = self.recommend(u)
                        if self.uuCF:
                            print ('    Recommend item(s):', recommended_items, 'for user', u)
                        else: 
                            print ('    Recommend item', u, 'for user(s) : ', recommended_items)

                def print_recommendation2(self,a):
                    for u in range(self.n_users):
                        recommended_items = self.recommend(u)
                        if(a==u):
                            return (recommended_items)


            r_cols = ['user_id', 'item_id', 'rating']
            ratings = pd.read_csv("ex.txt", sep = ' ', names = r_cols, encoding='latin-1')
            Y_data = ratings.to_numpy()
            rs = CF(Y_data, k = 2, uuCF = 1)
            rs.fit()
            rs.print_recommendation()
            z = rs.print_recommendation2(int(uid))
            y = json.dumps(z)
            print (y)
            return y

if __name__ == '__main__':
    app.run(host = '0.0.0.0',port=3000, debug=True)